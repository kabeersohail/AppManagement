package com.weguard.appmanagement.utils

data class App(
    val downloadUrl: String,
    val checksum: String,
    val apkSize: Long,
    val packageName: String,
    val appName: String
)