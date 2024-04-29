package com.lazyee.filescanner.klib

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.lazyee.filescanner.klib.config.ScanConfig
import com.lazyee.filescanner.klib.entity.ScanFile
import com.lazyee.filescanner.klib.handler.SimpleHandler
import com.lazyee.filescanner.klib.listener.OnFileScanListener
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:file scanner
 * Date: 2022/3/31 3:38 下午
 */
private const val TAG = "FileScanner"
class FileScanner(private val mContext: Context,) {
    private lateinit var scanConfig: ScanConfig
    private var mOnFileScanListener: OnFileScanListener? = null
    private val mExecutorService :ExecutorService = Executors.newSingleThreadExecutor()
    private val mHandler:SimpleHandler = SimpleHandler()
    companion object{
        fun with(context: Context): FileScanner {
            return FileScanner(context)
        }
    }

    fun setScanConfig(config: ScanConfig): FileScanner {
        scanConfig = config
        return this
    }

    fun setFileScanListener(listener: OnFileScanListener): FileScanner {
        mOnFileScanListener = listener
        return this
    }

    /**
     * 开始扫描
     * @params context Context
     * @params scanConfigFilePath String 扫描配置文件路径
     */
    fun start() {
        mOnFileScanListener?.onFileScanStart()
        mExecutorService.submit {
            try {
                val scanFileList = mutableListOf<ScanFile>()
                val timeCost = measureTimeMillis {
                    getAllExternalDirs().forEach {
                        val sdcardRootPath = it.absolutePath
                        scanConfig.provideSpecifiedDirs().forEach { dir ->
                            mergeFileList(
                                scanFileList,
                                deepFetchScanFile(generatePath(sdcardRootPath, dir))
                            )
                        }
                        //遍历根目录下的疑似文件夹和根目录下的文件
                        mergeFileList(scanFileList, deepFetchSuspectedDir(sdcardRootPath))
                    }
                    //            查找ContentProvider中的文件
                    mergeFileList(scanFileList, queryFile(mContext))

                    //倒序排序一下
                    scanFileList.sortByDescending { it.getLastModified() }
                    mHandler.callback { mOnFileScanListener?.onFileScanEnd(scanFileList) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mHandler.callback { mOnFileScanListener?.onFileScanError(e) }
            }
        }
    }
    /**
     * 生成ScanFile
     */
    private fun createScanFile(filePath: String): ScanFile?{
        val file = File(filePath)
        if(!file.exists()) return null
        if(!file.isFile)return null
        return createScanFile(file)
    }

    /**
     * 生成AudioFile
     */
    fun createScanFile(file: File): ScanFile?{
        val suffix = ScanFile.getSuffix(file)
        if(TextUtils.isEmpty(suffix))return null
        if(!scanConfig.provideFileSuffix().contains(suffix))return null

        return  ScanFile(file)
    }

    private fun queryFile(context: Context):List<ScanFile>{
        val result = mutableListOf<ScanFile>()
        result.addAll(realQueryFile(context,MediaStore.Files.getContentUri("external")))
        result.addAll(realQueryFile(context,MediaStore.Files.getContentUri("internal")))
        return result
    }

    private fun realQueryFile(context: Context,uri:Uri):List<ScanFile>{
        val scanFileList = mutableListOf<ScanFile>()
        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null, "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        )
        cursor?.run {
            while (moveToNext()) {
                val filePath = getString(getColumnIndex(MediaStore.Files.FileColumns.DATA))
//                LogUtils.e(TAG,"realQueryFile filePath:$filePath")
                if(scanFileList.find { it.getFilePath() == filePath } != null) continue
                createScanFile(filePath)?.run { scanFileList.add(this) }
            }
        }
        cursor?.close()
        return scanFileList
    }

    /**
     * 解析intent中的数据
     * @param intent
     * @return  返回文件路径 可能是真实路径，也有可能是uri
     */
    fun parseIntent(intent: Intent?): String? {
        intent ?: return null
        if (!TextUtils.isEmpty(intent.dataString)) {
            return intent.dataString
        }

        if (intent.clipData != null && intent.clipData!!.itemCount > 0) {
            val uri = intent.clipData!!.getItemAt(0).uri
            uri?:return null
            if(uri.toString().startsWith("content://media/")){
                val audioList = findFilePathListFormUri(mContext, uri)
                if (audioList.isNotEmpty()) {
                    return audioList.first()
                }
            }

            return uri.toString()
        }

        return null
    }

    /**
     * Uri查询文件路径
     */
    private fun findFilePathListFormUri(context: Context, uri: Uri): List<String> {
        val audioList = mutableListOf<String>()
        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null, "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )
        cursor?.run {
            while (moveToNext()) {
                audioList.add(getString(getColumnIndex(MediaStore.Audio.Media.DATA)))
            }
        }
        cursor?.close()
        return audioList
    }

    /**
     * 是否是无用文件夹
     */
    private fun isUnlessDir(file: File): Boolean {
        if(!isAvailableDir(file)) return true
        if(TextUtils.isEmpty(scanConfig.provideExcludeDirRegexp()))return false
        val dirName = file.absolutePath.substring(file.absolutePath.lastIndexOf(File.separator))
        return dirName.contains(Regex(scanConfig.provideExcludeDirRegexp()))
    }

    /**
     * 是否是疑似目标文件的文件夹
     */
    private fun isSuspectedAudioDir(file: File): Boolean {
        if(!isAvailableDir(file)) return false
        if(TextUtils.isEmpty(scanConfig.provideIncludeDirRegexp()))return false
        val dirName = file.absolutePath.substring(file.absolutePath.lastIndexOf(File.separator))
        return dirName.contains(Regex(scanConfig.provideIncludeDirRegexp()))
    }

    /**
     * 是否是可用文件夹
     */
    private fun isAvailableDir(file: File): Boolean {
        return file.isDirectory && !isHiddenDir(file.absolutePath)
    }

    /**
     * 是否是隐藏文件夹
     */
    private fun isHiddenDir(path: String):Boolean{
        val dirName = path.substring(path.lastIndexOf(File.separator) + 1)
        return dirName.startsWith(".")
    }

    /**
     * 合并音频列表
     */
    private fun mergeFileList(source: MutableList<ScanFile>, list: List<ScanFile>): MutableList<ScanFile> {
        if (source.isEmpty()) {
            source.addAll(list)
            return source
        }

        list.forEach { new -> source.find { it.getFilePath() == new.getFilePath() } ?: source.add(new)  }
        return source
    }

    /**
     * 拼接文件夹路径
     */
    private fun generatePath(vararg names: String): String = names.joinToString(separator = File.separator)

    private val fetchAudioList = mutableListOf<ScanFile>()

    /**
     * 递归遍历指定目录所有的扫描文件
     */
    private fun deepFetchScanFile(path:String):MutableList<ScanFile>{
        fetchAudioList.clear()
        deepFetchDirectory(path)
        return fetchAudioList
    }

    /**
     * 递归遍历所有的可能的音频文件
     */
    private fun deepFetchSuspectedDir(path: String): MutableList<ScanFile> {
        fetchAudioList.clear()
        deepFetchSuspectedDirectory(path)
        return fetchAudioList
    }

    /**
     * 遍历指定目录所有的文件(不包括文件夹)
     */
    private fun fetchTargetDir(path: String): MutableList<ScanFile> {
        fetchAudioList.clear()
        fetchDirectory(path)
        return fetchAudioList
    }

    /**
     * 递归遍历疑似文件夹下面的所有文件
     */
    private fun deepFetchSuspectedDirectory(path: String) {
        val file = File(path)
        if(!file.exists()) return
        if(file.isFile){
            createScanFile(file)?.run { fetchAudioList.add(this) }
            return
        }

        file.listFiles()?.forEach {
            if (isSuspectedAudioDir(it)) {
                deepFetchSuspectedDirectory(it.absolutePath)
                return@forEach
            }
            if(it.isFile) {
                createScanFile(it)?.run { fetchAudioList.add(this) }
            }
        }
    }

    /**
     * 递归遍历文件夹下面的所有文件
     */
    private fun deepFetchDirectory(path: String) {
        val file = File(path)
//        Log.e("leeorz","path:$path")
//        Log.e("leeorz","file.exists():${file.exists()}")
        if(!file.exists()) return
//        Log.e("leeorz",",file.isFile():${file.isFile}")
        if(file.isFile){
            createScanFile(file)?.run { fetchAudioList.add(this) }
            return
        }
//        Log.e("leeorz",",isUnlessDir:${isUnlessDir(file)}")
        if (isUnlessDir(file)) return
//        Log.e("leeorz",",file.listFiles():${file.listFiles()}")
        file.listFiles()?.forEach {
            if (!isUnlessDir(it)) {
                deepFetchDirectory(it.absolutePath)
                return@forEach
            }

            if(it.isFile){
                createScanFile(it)?.run { fetchAudioList.add(this) }
            }
        }
    }

    /**
     * 遍历文件夹下面的所有文件
     */
    private fun fetchDirectory(path: String) {
        val file = File(path)
        if(!file.exists()) return
        if (file.isDirectory) {
            file.listFiles()?.forEach { createScanFile(it)?.run { fetchAudioList.add(this) } }
        }
    }

    /**
     * 获取挂载的所有外部文件夹
     */
    @SuppressLint("PrivateApi")
    private fun getAllExternalDirs(): List<File> {
        val dirs = mutableListOf<File>()
        try {
            val clazz = Environment::class.java
            val currentUserField = clazz.getDeclaredField("sCurrentUser")
            currentUserField.isAccessible = true
            val userEnvironmentInstance = currentUserField.get(null)

            val userEnvironment = Class.forName("android.os.Environment\$UserEnvironment")
            val getExternalDirsMethod = userEnvironment.getDeclaredMethod("getExternalDirs")
            val fileArr = getExternalDirsMethod.invoke(userEnvironmentInstance) as Array<File>
            dirs.addAll(fileArr)
        } catch (e: Exception) {
            e.printStackTrace()
            dirs.add(Environment.getExternalStorageDirectory())
        }
        getDualOpenRootFile()?.run {
            dirs.add(this)
        }

        return dirs
    }

    /**
     * 获取引用双开的文件夹
     */
    private fun getDualOpenRootFile(): File? {
        val file = File(
            Environment.getExternalStorageDirectory().absolutePath.replace(
                "${File.separator}0",
                "${File.separator}999"
            )
        )
        return if (file.exists()) file else null
    }
}




