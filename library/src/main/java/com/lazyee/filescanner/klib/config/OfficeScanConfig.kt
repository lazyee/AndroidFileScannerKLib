package com.lazyee.filescanner.klib.config

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:office文件格式
 * Date: 2024/4/29 22:13
 */
open class OfficeScanConfig :DefaultScanConfig() {
    override fun provideFileSuffix(): Array<String> {
        return arrayOf("doc","docx","ppt","pptx","xls","xlsx")
    }
}