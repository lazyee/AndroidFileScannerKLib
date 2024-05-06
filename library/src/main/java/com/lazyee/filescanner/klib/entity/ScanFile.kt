package com.lazyee.filescanner.klib.entity

import java.io.File

class ScanFile{
    val realFile:File

    constructor(filePath: String){
       realFile = File(filePath)
    }
    constructor(file: File){
        this.realFile = file
    }

    fun getFilePath():String{
        return realFile.absolutePath
    }

    fun getName(): String {
        return realFile.name
    }

    fun getLastModified():Long{
        return realFile.lastModified()
    }

    fun getSuffix(): String {
        return getSuffix(realFile)
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
