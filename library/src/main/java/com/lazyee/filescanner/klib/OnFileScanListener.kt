package com.lazyee.filescanner.klib

import java.lang.Exception

interface OnFileScanListener {
    fun onFileScanStart()
    fun onFileScan(file:ScanFile)
    fun onFileScanEnd(fileList:List<ScanFile>)
    fun onFileScanError(e:Exception)
}