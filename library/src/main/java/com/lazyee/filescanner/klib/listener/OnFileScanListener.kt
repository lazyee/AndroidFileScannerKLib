package com.lazyee.filescanner.klib.listener

import com.lazyee.filescanner.klib.entity.ScanFile
import java.lang.Exception

interface OnFileScanListener {
    fun onFileScanStart()
    fun onFileScanEnd(fileList:List<ScanFile>)
    fun onFileScanError(e:Exception)
}