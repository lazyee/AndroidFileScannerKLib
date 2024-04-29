package com.lazyee.filescanner.klib

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:音频扫描配置
 * Date: 2022/6/2 2:20 下午
 */
class ScanConfig(private val configJson:String) {
    var supportFileType: MutableList<String> = mutableListOf()//支持的文件后缀
    var unlessDirRegexp: String = ""//无用文件夹正则表达式
    var suspectedDirRegexp: String = ""//疑似文件夹正则表达式
    var scanTargetDirs: MutableList<String> = mutableListOf()//扫描的目标文件夹

    fun getDisplaySupportFileType(): String {
        val str = supportFileType.toString()
        return str.substring(1, str.length - 1)
    }

    init {
        parseScanConfig()
    }

    /**
     * 解析扫描配置json
     */
    private fun parseScanConfig() {
        val jsonObject = JSONObject(configJson)
        unlessDirRegexp = jsonObject.optString("unlessDirRegexp", "")
        suspectedDirRegexp = jsonObject.optString("suspectedDirRegexp", "")
        val supportFileTypeList = jsonObject.optJSONArray("supportFileType")
        supportFileTypeList?.run {
            repeat(length()) {
                supportFileType.add(getString(it))
            }
        }
        val dirs: JSONArray? = jsonObject.optJSONArray("scanTargetDirs")
        dirs?.run {
            repeat(length()) {
                scanTargetDirs.add(getString(it))
            }
        }
    }

    companion object {



        /**
         * 获取当前的扫描配置
         */
        fun getDefaultScanConfig(context: Context): ScanConfig{
            return ScanConfig(getDefaultScanConfigJson(context))
        }
        private fun getDefaultScanConfigJson(context:Context): String {
            val inputSteam = context.assets.open("default_scan_config.json")
            val bufferedInputStream = BufferedInputStream(inputSteam)
            return bufferedInputStream.reader().readText()
        }
    }
}


