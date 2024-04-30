package com.lazyee.filescanner.klib.config

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:图片
 * Date: 2024/4/29 22:13
 */
open class ImageScanConfig :DefaultScanConfig() {
    override fun provideFileSuffix(): Array<String> {
        return arrayOf("png","jpg","jpeg","bmp","gif")
    }
}