package com.lazyee.filescanner.demo.adapter

import androidx.recyclerview.widget.RecyclerView
import com.lazyee.filescanner.klib.entity.ScanFile

abstract class FileAdapter<VH :RecyclerView.ViewHolder> :RecyclerView.Adapter<VH>(){
    protected var mOnClickCallback:((ScanFile)->Unit)? = null
    fun setItemClick(callback:(ScanFile)->Unit){
       mOnClickCallback = callback
    }
}