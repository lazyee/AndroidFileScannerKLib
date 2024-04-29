package com.lazyee.filescanner.klib

import java.io.File

data class ScanFile(val file : File){

    fun getFilePath():String{
        return file.absolutePath
    }

    fun getLastModified():Long{
        return file.lastModified()
    }

    companion object{
        fun getSuffix(file:File):String{
            return getSuffix(file.name)
        }

        fun getSuffix(fileName:String):String{
            val lastIndex = fileName.lastIndexOf(".")
            if (lastIndex < 0 || lastIndex >= fileName.length - 1) {
                return ""
            }
            return fileName.substring(lastIndex + 1, fileName.length).toLowerCase()
        }
    }
}
