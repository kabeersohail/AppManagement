package com.weguard.appmanagement

import android.util.Log
import com.weguard.appmanagement.utils.App
import com.weguard.appmanagement.utils.AppDownloadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class AppManagementV2(
    private val apps: List<App>
) {

    suspend fun execute() {
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
        Log.d("DownloadLogic", "$app")
    }


    internal fun downloadApps(apps: List<App>): Flow<AppDownloadResult> {
        Log.d("DownloadLogic", "$apps")

        // download logic goes here
        return flow {
            emit(AppDownloadResult.Success(apps.first()))
        }
    }

    internal fun appIsNotInstalled(packageName: String): Boolean = Random.nextBoolean().also {
        Log.d("DownloadLogic", packageName)
    }

    internal fun installApp(packageName: String): Boolean = Random.nextBoolean().also {
        Log.d("DownloadLogic", packageName)
    }

    @Suppress("unused")
    private fun installApps(apps: List<App>) {
        apps.filter { app -> appIsNotInstalled(app.packageName) }.forEach { notInstalledApp ->
            installApp(notInstalledApp.packageName)
        }
    }
}