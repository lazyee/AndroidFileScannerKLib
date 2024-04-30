package com.lazyee.filescanner.demo

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazyee.filescanner.demo.adapter.FileGridAdapter
import com.lazyee.filescanner.demo.adapter.FileListAdapter
import com.lazyee.filescanner.demo.databinding.ActivityOfficeFileListBinding
import com.lazyee.filescanner.klib.FileScanner
import com.lazyee.filescanner.klib.listener.OnFileScanListener
import com.lazyee.filescanner.klib.entity.ScanFile
import com.lazyee.filescanner.klib.config.OfficeScanConfig
import com.lazyee.klib.base.ViewBindingActivity
import com.lazyee.klib.util.DateUtils
import com.lazyee.klib.util.LogUtils
import java.lang.Exception
import java.util.Date

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:
 * Date: 2024/4/29 22:16
 */
class OfficeFileListActivity : ViewBindingActivity<ActivityOfficeFileListBinding>() ,
    OnFileScanListener {
    private val scanFileList = mutableListOf<ScanFile>()
    private val fileListAdapter by lazy { FileListAdapter(scanFileList) }
    private val fileGridAdapter by lazy { FileGridAdapter(scanFileList) }
    override fun initView() {
        super.initView()

        mViewBinding.run {
            recyclerView.adapter = fileListAdapter
            btnList.setOnClickListener {
                recyclerView.layoutManager = LinearLayoutManager(this@OfficeFileListActivity)
                recyclerView.adapter = fileListAdapter
            }
            btnGrid.setOnClickListener {
                recyclerView.layoutManager = GridLayoutManager(this@OfficeFileListActivity,3)
                recyclerView.adapter = fileGridAdapter
            }
        }

        startScan()
    }

    private fun startScan(){
        FileScanner.with(this)
            .setFileScanListener(this)
//            .setScanConfig(DefaultScanConfig.createScanConfigFromAsset(this,"scan_config.json"))
            .setScanConfig(OfficeScanConfig())
//            .setScanConfig(MyOfficeScanConfig())
            .start()
    }

    private class MyOfficeScanConfig:OfficeScanConfig(){
        override fun provideEndTime(): Long? {
//            return System.currentTimeMillis() - 1 * 24 * 3600 * 1000
            return null
        }
//
        override fun provideStartTime(): Long? {
            return System.currentTimeMillis() - 30L * 24 * 3600 * 1000
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