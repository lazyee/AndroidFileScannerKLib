package com.lazyee.filescanner.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.lazyee.filescanner.klib.FileScanner
import com.lazyee.filescanner.klib.ScanConfig
import com.lazyee.klib.base.BaseActivity
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        XXPermissions.with(this)
            .permission(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(OnPermissionCallback { p0, p1 ->
                if(p1){
                    FileScanner(this,ScanConfig.getDefaultScanConfig(this)).scan()
                }
            })

    }
}
