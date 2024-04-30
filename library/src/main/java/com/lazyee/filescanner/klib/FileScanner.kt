package com.lazyee.filescanner.klib

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import com.lazyee.filescanner.klib.config.ScanConfig
import com.lazyee.filescanner.klib.entity.ScanFile
import com.lazyee.filescanner.klib.handler.SimpleHandler
import com.lazyee.filescanner.klib.listener.OnFileScanListener
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:file scanner
 * Date: 2022/3/31 3:38 下午
 */
private const val TAG = "[FileScanner]"
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
                val filePathList = mutableListOf<String>()
                getAllExternalDirs().forEach {
                    val sdcardRootPath = it.absolutePath
                    //扫描根目录下的所有文件
                    mergeFileList(scanFileList, fetchDirectory(sdcardRootPath,filePathList))
                    filePathList.clear()

                    //扫描指定目标的文件
                    scanConfig.provideSpecifiedDirs().forEach { dir ->
                        mergeFileList(scanFileList, deepFetchDirectory(generatePath(sdcardRootPath, dir),filePathList))
                        filePathList.clear()
                    }

                    //遍历根目录下的疑似文件夹和根目录下的文件
                    mergeFileList(scanFileList, deepFetchIncludeDirectory(sdcardRootPath,filePathList))
                    filePathList.clear()
                }
                //查找ContentProvider中的文件
                mergeFileList(scanFileList, queryFile(mContext))

                //倒序排序
                scanFileList.sortByDescending { it.getLastModified() }
                mHandler.callback { mOnFileScanListener?.onFileScanEnd(scanFileList) }
            } catch (e: Exception) {
                e.printStackTrace()
                mHandler.callback { mOnFileScanListener?.onFileScanError(e) }
            }
        }
    }

    private fun isTargetFile(filePath: String): Boolean {
        val file = File(filePath)
        if(!file.exists()) return false
        if(!file.isFile)return false
        return isTargetFile(file)
    }

    private fun isTargetFile(file: File): Boolean {
        val suffix = ScanFile.getSuffix(file)
        if(TextUtils.isEmpty(suffix))return false
        return scanConfig.provideFileSuffix().contains(suffix)
    }

    private fun queryFile(context: Context):List<String>{
        val result = mutableListOf<String>()
        result.addAll(realQueryFile(context,MediaStore.Files.getContentUri("external")))
        result.addAll(realQueryFile(context,MediaStore.Files.getContentUri("internal")))
        return result
    }

    private fun realQueryFile(context: Context,uri:Uri):List<String>{
        val filePathList = mutableListOf<String>()
        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null, "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        )
        cursor?.run {
            while (moveToNext()) {
                val filePath = getString(getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val time = getLong(getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)) * 1000L
                if(!compareFileTime(time)) continue
                if(filePathList.find { it == filePath } != null) continue
                if(isTargetFile(filePath)){
                    filePathList.add(filePath)
                }
            }
        }
        cursor?.close()
        return filePathList
    }

    private fun compareFileTime(time: Long):Boolean{
        val startTime = scanConfig.provideStartTime()
        val endTime = scanConfig.provideEndTime()

        if(startTime == null && endTime == null) return true
        if(startTime != null && startTime >= time) return false
        if(endTime == null) return true
        if(endTime <= time) return false
        return true
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
                val filePathList = findFilePathListFormUri(mContext, uri)
                if (filePathList.isNotEmpty()) {
                    return filePathList.first()
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
        val filePathList = mutableListOf<String>()
        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null, "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )
        cursor?.run {
            while (moveToNext()) {
                filePathList.add(getString(getColumnIndex(MediaStore.Audio.Media.DATA)))
            }
        }
        cursor?.close()
        return filePathList
    }

    /**
     * 是否是排除扫描的文件夹
     */
    private fun isExcludeScanDir(file: File): Boolean {
        if(!isAvailableDir(file)) return true
        if(TextUtils.isEmpty(scanConfig.provideExcludeDirRegexp()))return false
        val dirName = file.absolutePath.substring(file.absolutePath.lastIndexOf(File.separator))
        return dirName.contains(Regex(scanConfig.provideExcludeDirRegexp()))
    }

    /**
     * 是否包含扫描的文件夹
     */
    private fun isIncludeScanDir(file: File): Boolean {
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
    private fun mergeFileList(source: MutableList<ScanFile>, filePathList: List<String>){
        if (source.isEmpty()) {
            filePathList.forEach { path->
                if(!compareFileTime(File(path).lastModified())) return@forEach
                source.add(ScanFile(path))
            }
            return
        }

        filePathList.forEach { path ->
            if(!compareFileTime(File(path).lastModified())) return@forEach
            source.find { it.getFilePath() == path } ?: source.add(ScanFile(path))
        }
    }

    /**
     * 拼接文件夹路径
     */
    private fun generatePath(vararg names: String): String = names.joinToString(separator = File.separator)



    /**
     * 递归遍历包含文件夹下面的所有文件
     */
    private fun deepFetchIncludeDirectory(path: String, filePathList:MutableList<String>):List<String> {
        val file = File(path)
        if(!file.exists()) return filePathList
        if(file.isFile){
            if(isTargetFile(file)){
                filePathList.add(file.absolutePath)
            }
            return filePathList
        }

        file.listFiles()?.forEach {
            if (isIncludeScanDir(it)) {
                deepFetchIncludeDirectory(it.absolutePath,filePathList)
                return@forEach
            }
            if(it.isFile) {
                if(isTargetFile(it)){
                    filePathList.add(it.absolutePath)
                }
            }
        }

        return filePathList
    }

    /**
     * 递归遍历文件夹下面的所有文件
     */
    private fun deepFetchDirectory(path: String, filePathList:MutableList<String>): MutableList<String> {
        val file = File(path)
        if(!file.exists()) return filePathList
        if(file.isFile){
            if(isTargetFile(file)){
                filePathList.add(file.absolutePath)
            }
            return filePathList
        }
        if (isExcludeScanDir(file)) return filePathList
        file.listFiles()?.forEach {
            if (!isExcludeScanDir(it)) {
                deepFetchDirectory(it.absolutePath,filePathList)
                return@forEach
            }

            if(it.isFile){
                if(isTargetFile(it)) {
                    filePathList.add(it.absolutePath)
                }
            }
        }
        return filePathList
    }

    /**
     * 遍历文件夹下面的所有文件(不包括文件夹)
     */
    private fun fetchDirectory(path: String, filePathList: MutableList<String>): MutableList<String> {
        val file = File(path)
        if(!file.exists()) return filePathList
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                if(isTargetFile(it)){
                    filePathList.add(it.absolutePath)
                }
            }
        }
        return filePathList
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




