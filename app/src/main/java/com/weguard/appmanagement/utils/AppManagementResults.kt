package com.weguard.appmanagement.utils

sealed class AppDownloadResult {
    data class Success(val app: App) : AppDownloadResult()
    data class Failure(val app: App, val reason: String) : AppDownloadResult()
}

sealed class ApkInstallationResult {
    data class Success(val app: App) : ApkInstallationResult()
    data class Failure(val app: App, val reason: String) : ApkInstallationResult()
}

sealed class AppManagementResult {
    object Success : AppManagementResult()
    data class Failure(val failedApkMapList: List<Pair<App, String>>) : AppManagementResult()
}