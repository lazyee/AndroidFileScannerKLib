package com.lazyee.filescanner.klib.config

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:
 * Date: 2024/4/29 21:30
 */
interface ScanConfig {

    /**
     * 开始时间
     */
    fun provideStartTime(): Long? = null

    /**
     * 结束时间
     */
    fun provideEndTime(): Long? = null

    /**
     * 目标文件后缀名
     */
    fun provideFileSuffix():Array<String>

    /**
     * 排除文件夹正则表达式
     */
    fun provideExcludeDirRegexp(): String

    /**
     * 包含文件夹正则表达式
     */
    fun provideIncludeDirRegexp(): String

    /**
     * 扫描的目标文件夹
     */
    fun provideSpecifiedDirs(): Array<String>

}