package com.lazyee.filescanner.klib.entity

import java.io.File

class ScanFile{
    private val file:File

    constructor(filePath: String){
       file = File(filePath)
    }
    constructor(file: File){
        this.file = file
    }

    fun getFilePath():String{
        return file.absolutePath
    }

    fun getName(): String {
        return file.name
    }

    fun getLastModified():Long{
        return file.lastModified()
    }

    fun getSuffix(): String {
        return getSuffix(file)
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
