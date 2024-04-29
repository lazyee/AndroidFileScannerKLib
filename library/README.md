```json
{
  "supportFileType":["doc","docx","ppt","pptx","xls","xlsx","pdf"],
  "unlessDirRegexp":"(image)|(IMAGE)|(imagecache)|(imageCache)|(encrypt)|(ENCRYPT)|(decrypt)|(DECRYPT)|(request)|(REQUEST)|(thumb)|(THUMB)|(apk)|(APK)|(error)|(mail)|(MAIL)|(log)|(xlog)|(video)|(VIDEO)|(Video)|(videocache)|(icon)|(photo)|(PHOTO)|(Photo)",
  "suspectedDirRegexp":"(record)|(Record)|(RECORD)|(sound)|(Sound)|(SOUND)|(mp3)|(MP3)|(audio)|(AUDIO)|(音频)|(资料)|(会议)|(录音)|(记录)|(文档)|(文件)",
  "scanTargetDirs":[
    "MIUI/sound_recorder",
    "Music/Recordings",
    "Android/data/com.tencent.mm/MicroMsg/Download",
    "tencent/MicroMsg/WeiXin",
    "Android/data/com.alibaba.android.rimet",
    "DingTalk",
    "Android/data/com.tencent.wework/files/filecache",
    "Android/data/com.tencent.mobileqq/Tencent/QQfile_recv",
    "tencent/QQfile_recv",
    "tencent/QQmail",
    "Download/QQMail",
    "Download"
  ]
}
```
`scanTargetDirs`:一定要扫描的文件夹  
`supportFileType`:支持的文件格式  
`unlessDirRegexp`:表示的是无用文件夹的正则表达式  
`suspectedAudioRegexp`:表示的是疑似有用的正则表达式  
