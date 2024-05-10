package com.lazyee.filescanner.demo

import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.lazyee.filescanner.demo.databinding.ActivityMainBinding
import com.lazyee.klib.base.ViewBindingActivity
import com.lazyee.klib.extension.goto

class MainActivity : ViewBindingActivity<ActivityMainBinding>(){

    override fun initView() {
        super.initView()

        XXPermissions.with(this)
            .permission(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(OnPermissionCallback { _, _ -> })

        mViewBinding.run {
            btnOfficeFileList.setOnClickListener { goto(OfficeFileListActivity::class.java) }
            btnImageFileList.setOnClickListener { goto(ImageFileListActivity::class.java) }
            btnPdfFileList.setOnClickListener { goto(PdfFileListActivity::class.java) }
            btnZipFileList.setOnClickListener { goto(ZipFileListActivity::class.java) }
            btnAudioList.setOnClickListener { goto(AudioListActivity::class.java) }
        }
    }


}
