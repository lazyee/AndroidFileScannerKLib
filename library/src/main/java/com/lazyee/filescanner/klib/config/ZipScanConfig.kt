package com.lazyee.filescanner.klib.config

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:压缩包
 * Date: 2024/4/29 22:13
 */
class ZipScanConfig :DefaultScanConfig() {
    override fun provideFileSuffix(): Array<String> {
        return arrayOf("zip","rar","7z")
    }
}