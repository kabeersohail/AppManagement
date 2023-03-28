package com.weguard.appmanagement.utils

sealed class DownloadResult {
    data class Success(val app: App) : DownloadResult()
    data class Failure(val app: App, val reason: String) : DownloadResult()
}

sealed class ApkInstallationResult {
    data class Success(val app: App) : ApkInstallationResult()
    data class Failure(val app: App, val reason: String) : ApkInstallationResult()
}

sealed class AppManagementResult {
    object Success : AppManagementResult()
    data class Failure(val failedApkMapList: List<Pair<App, String>>) : AppManagementResult()
}