package com.lazyee.filescanner.klib.config

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
open class DefaultScanConfig : ScanConfig {

    override fun provideFileSuffix(): Array<String> {
        return arrayOf("doc","docx","ppt","pptx","xls","xlsx","pdf","png","txt","jpeg","jpg")
    }

    override fun provideExcludeDirRegexp(): String {
        return ""
    }

    override fun provideIncludeDirRegexp(): String {
        return ""
    }

    override fun provideSpecifiedDirs(): Array<String> {
        return arrayOf(
            "Android/data/com.tencent.mm/MicroMsg/Download",//微信
            "tencent/MicroMsg/WeiXin",
            "Android/data/com.alibaba.android.rimet",//钉钉
            "DingTalk",
            "Android/data/com.tencent.wework/files/filecache",//企业微信
            "Android/data/com.tencent.mobileqq/Tencent/QQfile_recv",//QQ
            "tencent/QQfile_recv",
            "tencent/QQmail",//QQ邮箱
            "Download/QQMail",
            "Download",//下载
        )
    }

    companion object {

        fun createScanConfig(configJson:String): ScanConfig {
            val jsonObject = JSONObject(configJson)
            val excludeDirRegexp = jsonObject.optString("excludeDirRegexp", "")
            val includeDirRegexp = jsonObject.optString("includeDirRegexp", "")
            val starTime = jsonObject.optLong("startTime",-1)
            val endTime = jsonObject.optLong("endTime",-1)
            val fileSuffixJsonArray = jsonObject.optJSONArray("fileSuffix")
            val fileSuffix =Array<String>(fileSuffixJsonArray?.length()?:0) {""}

            fileSuffixJsonArray?.run {
                repeat(length()) {
                    fileSuffix[it] = getString(it)
                }
            }
            val specifiedDirsJsonArray: JSONArray? = jsonObject.optJSONArray("specifiedDirs")
            val specifiedDirs =Array<String>(specifiedDirsJsonArray?.length()?:0) {""}
            specifiedDirsJsonArray?.run {
                repeat(length()) {
                    specifiedDirs[it] = getString(it)
                }
            }

            return object : ScanConfig{
                override fun provideFileSuffix(): Array<String> {
                    return fileSuffix
                }

                override fun provideExcludeDirRegexp(): String {
                    return excludeDirRegexp
                }

                override fun provideIncludeDirRegexp(): String {
                    return includeDirRegexp
                }

                override fun provideSpecifiedDirs(): Array<String> {
                    return specifiedDirs
                }

                override fun provideStartTime(): Long? {
                    return if(starTime < 0) null else starTime
                }

                override fun provideEndTime(): Long? {
                    return if(endTime < 0) null else endTime
                }

            }
        }

        fun createScanConfigFromAsset(context: Context,assetJsonFilePath:String): ScanConfig {
            val inputSteam = context.assets.open(assetJsonFilePath)
            val bufferedInputStream = BufferedInputStream(inputSteam)
            val configJson = bufferedInputStream.reader().readText()
            return createScanConfig(configJson)
        }
    }
}


