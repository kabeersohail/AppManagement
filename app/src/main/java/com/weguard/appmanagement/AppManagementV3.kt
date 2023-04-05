package com.weguard.appmanagement

import com.weguard.appmanagement.utils.App
import com.weguard.appmanagement.utils.AppDownloadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class AppManagementV3(
    private val apps: List<App>
) {

    suspend fun execute() {

        if(apps.isEmpty()) {
            return
        }

        downloadApps(apps).collect { downloadResult ->
            when(downloadResult) {
                is AppDownloadResult.Failure -> {
                    handleDownloadFailure(downloadResult.app)
                }

                is AppDownloadResult.Success -> {
                    installApp(downloadResult.app.packageName)
                }
            }
        }
    }

    internal fun handleDownloadFailure(app: App) {
    }


    fun downloadApps(apps: List<App>): Flow<AppDownloadResult> {

        // download logic goes here
        return flow {
            emit(AppDownloadResult.Success(apps.first()))
        }
    }

    internal fun appIsNotInstalled(packageName: String): Boolean = Random.nextBoolean().also {
    }

    internal fun installApp(packageName: String): Boolean = Random.nextBoolean().also {
    }

    @Suppress("unused")
    private fun installApps(apps: List<App>) {
        apps.filter { app -> appIsNotInstalled(app.packageName) }.forEach { notInstalledApp ->
            installApp(notInstalledApp.packageName)
        }
    }
}