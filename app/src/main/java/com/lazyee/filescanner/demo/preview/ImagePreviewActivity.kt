package com.lazyee.filescanner.demo.preview

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.lazyee.filescanner.demo.databinding.ActivityImagePreviewBinding
import com.lazyee.klib.base.ViewBindingActivity

class ImagePreviewActivity : ViewBindingActivity<ActivityImagePreviewBinding>() {

    companion object{
        fun gotoThis(context: Context,filePath:String){
            val intent = Intent(context,ImagePreviewActivity::class.java)
            intent.putExtra("filePath",filePath)
            context.startActivity(intent)
        }
    }

    private val imageFilePath by lazy { intent.getStringExtra("filePath") }

    override fun initView() {
        super.initView()

        Glide.with(this).load(imageFilePath).into(mViewBinding.ivPreview)

    }
}