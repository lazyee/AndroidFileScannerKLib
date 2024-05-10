package com.lazyee.filescanner.klib.config

open class AudioScanConfig :DefaultScanConfig() {
    override fun provideFileSuffix(): Array<String> {
        return arrayOf("mp3","m4a","wav","aac")
    }

    override fun provideExcludeDirRegexp(): String {
        return "(image)|(IMAGE)|(imagecache)|(imageCache)|(encrypt)|(ENCRYPT)|(decrypt)|(DECRYPT)|(request)|(REQUEST)|(thumb)|(THUMB)|(apk)|(APK)|(error)|(mail)|(MAIL)|(log)|(xlog)|(video)|(VIDEO)|(Video)|(videocache)|(icon)|(photo)|(PHOTO)|(Photo)"
    }

    override fun provideIncludeDirRegexp(): String {
        return "(record)|(Record)|(RECORD)|(sound)|(Sound)|(SOUND)|(mp3)|(MP3)|(audio)|(AUDIO)|(音频)|(资料)|(会议)|(录音)|(记录)|(文档)|(文件)"
    }

    override fun provideSpecifiedDirs(): Array<String> {
        return arrayOf(
            "MIUI/sound_recorder",//录音
            "Music/Recordings",
            "Android/data/com.tencent.mm/MicroMsg/Download",//微信
            "tencent/MicroMsg/WeiXin",
            "Android/data/com.alibaba.android.rimet",//钉钉
            "DingTalk",
            "Android/data/com.tencent.wework/files/filecache",//企业微信
            "Android/data/com.tencent.mobileqq/Tencent/QQfile_recv",//QQ
            "tencent/QQfile_recv",
            "tencent/QQmail",//QQ邮箱
            "Download/QQMail",
            "Download",//下载
            "Music/Lark",//飞书
        )
    }
}