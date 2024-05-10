package com.lazyee.filescanner.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lazyee.filescanner.demo.R
import com.lazyee.filescanner.demo.databinding.ItemListFileBinding
import com.lazyee.filescanner.klib.entity.ScanFile
import com.lazyee.klib.util.DateUtils

/**
 * Author: leeorz
 * Email: 378229364@qq.com
 * Description:
 * Date: 2024/4/29 22:44
 */
class FileListAdapter(private val scanFileList:List<ScanFile>): FileAdapter<FileListAdapter.ListItemViewHolder>(){
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ListItemViewHolder {
        return ListItemViewHolder(ItemListFileBinding.inflate(LayoutInflater.from(viewGroup.context)))
    }

    override fun getItemCount(): Int {
        return scanFileList.size
    }

    override fun onBindViewHolder(viewHolder: ListItemViewHolder, position: Int) {
        viewHolder.bind(scanFileList[position])
    }

    inner class ListItemViewHolder(private val mBinding: ItemListFileBinding): RecyclerView.ViewHolder(mBinding.root){

        private fun getIconResId(file: ScanFile): Int {
            when (file.getSuffix()){
                "doc",
                "docx"-> return R.drawable.ic_word
                "pdf" -> return R.drawable.ic_pdf
                "ppt",
                "pptx"-> return R.drawable.ic_ppt
                "xls",
                "xlsx"-> return R.drawable.ic_excel
                "png"-> return R.drawable.ic_png
                "jpeg",
                "jpg"-> return R.drawable.ic_jpg
                "zip"-> return R.drawable.ic_zip
                "aac",
                "mp3",
                "m4a",
                "wav"-> return R.drawable.ic_audio
            }
            return R.drawable.ic_other
        }

        fun bind(file: ScanFile){
            mBinding.run {
                root.setOnClickListener { mOnClickCallback?.invoke(file) }
                ivIcon.setImageResource(getIconResId(file))
                tvFileName.text = file.getName()
                tvFilePath.text = file.getFilePath()
                tvLastModiyset.text = DateUtils.format(file.getLastModified(),DateUtils.yyyyMMddHHmmss)
            }
        }
    }
}

