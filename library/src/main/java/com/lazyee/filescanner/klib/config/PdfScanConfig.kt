package com.lazyee.filescanner.klib.config

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:pdf
 * Date: 2024/4/29 22:13
 */
class PdfScanConfig :DefaultScanConfig() {
    override fun provideFileSuffix(): Array<String> {
        return arrayOf("pdf")
    }
}