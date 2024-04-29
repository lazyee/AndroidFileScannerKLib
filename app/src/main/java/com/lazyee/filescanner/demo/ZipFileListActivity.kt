package com.lazyee.filescanner.demo

import android.annotation.SuppressLint
import android.util.Log
import com.lazyee.filescanner.demo.adapter.FileAdapter
import com.lazyee.filescanner.demo.databinding.ActivityZipFileListBinding
import com.lazyee.filescanner.klib.FileScanner
import com.lazyee.filescanner.klib.listener.OnFileScanListener
import com.lazyee.filescanner.klib.entity.ScanFile
import com.lazyee.filescanner.klib.config.ZipScanConfig
import com.lazyee.klib.base.ViewBindingActivity
import java.lang.Exception

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:
 * Date: 2024/4/29 22:16
 */
class ZipFileListActivity : ViewBindingActivity<ActivityZipFileListBinding>() , OnFileScanListener {
    private val scanFileList = mutableListOf<ScanFile>()
    private val fileAdapter by lazy { FileAdapter(scanFileList) }

    override fun initView() {
        super.initView()

        mViewBinding.recyclerView.adapter = fileAdapter

        startScan()
    }

    private fun startScan(){
        FileScanner.with(this)
            .setFileScanListener(this)
            .setScanConfig(ZipScanConfig())
            .start()
    }

    override fun onFileScanStart() {
        Log.e("TAG","onFileScanStart")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFileScanEnd(fileList: List<ScanFile>) {
        Log.e("TAG","onFileScanEnd:${fileList.size}")
        scanFileList.clear()
        scanFileList.addAll(fileList)
        fileAdapter.notifyDataSetChanged()
    }

    override fun onFileScanError(e: Exception) {
        Log.e("TAG","onFileScanError:${e.toString()}")
    }

}