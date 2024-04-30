package com.lazyee.filescanner.demo

import android.annotation.SuppressLint
import android.util.Log
import com.lazyee.filescanner.demo.adapter.FileListAdapter
import com.lazyee.filescanner.demo.databinding.ActivityImageFileListBinding
import com.lazyee.filescanner.demo.preview.ImagePreviewActivity
import com.lazyee.filescanner.klib.FileScanner
import com.lazyee.filescanner.klib.listener.OnFileScanListener
import com.lazyee.filescanner.klib.entity.ScanFile
import com.lazyee.filescanner.klib.config.ImageScanConfig
import com.lazyee.klib.base.ViewBindingActivity
import java.lang.Exception

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:
 * Date: 2024/4/29 22:16
 */
class ImageFileListActivity : ViewBindingActivity<ActivityImageFileListBinding>() ,
    OnFileScanListener {
    private val scanFileList = mutableListOf<ScanFile>()
    private val fileListAdapter by lazy { FileListAdapter(scanFileList) }

    override fun initView() {
        super.initView()

        fileListAdapter.setItemClick {
            ImagePreviewActivity.gotoThis(this@ImageFileListActivity,it.getFilePath())
        }
        mViewBinding.recyclerView.adapter = fileListAdapter

        startScan()
    }

    private fun startScan(){
        FileScanner.with(this)
            .setFileScanListener(this)
            .setScanConfig(MyImageScanConfig())
            .start()
    }

    private class MyImageScanConfig:ImageScanConfig(){
        override fun provideStartTime(): Long? {
//            return System.currentTimeMillis() - 30L * 24 * 3600 * 1000
            return null
        }

    }

    override fun onFileScanStart() {
        Log.e("TAG","onFileScanStart")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFileScanEnd(fileList: List<ScanFile>) {
        Log.e("TAG","onFileScanEnd:${fileList.size}")
        scanFileList.clear()
        scanFileList.addAll(fileList)
        fileListAdapter.notifyDataSetChanged()
    }

    override fun onFileScanError(e: Exception) {
        Log.e("TAG","onFileScanError:${e.toString()}")
    }

}