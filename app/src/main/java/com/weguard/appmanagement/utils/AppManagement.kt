package com.weguard.appmanagement.utils

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class AppManagement(
    private val apps: List<App>
) {

    private val failedApkMapList = mutableListOf<Pair<App, String>>()
    private val installedApps = mutableListOf<App>()
    internal var downloadedApps: MutableList<App> = mutableListOf()

    suspend fun execute(): AppManagementResult {

        for (app in apps) {
            when (val downloadResult = downloadAPK(app)) {
                is DownloadResult.Success -> {
                    val isAdded = downloadedApps.add(downloadResult.app)

                    if(isAdded) {
                        Log.d("AppManagement", "added downloaded app to downloadedApps list")
                    }

                    installApps(downloadedApps, installedApps)
                }
                is DownloadResult.Failure -> {
                    failedApkMapList.add(downloadResult.app to downloadResult.reason)
                }
            }
        }
        return if (failedApkMapList.isNotEmpty()) {
            AppManagementResult.Failure(failedApkMapList)
        } else {
            AppManagementResult.Success
        }
    }

    private suspend fun installApps(
        downloadedApps: List<App>,
        installedApps: MutableList<App>
    ) {
        for (app in downloadedApps) {
            if (app !in installedApps) {
                when (val installResult = installApk(app)) {
                    is ApkInstallationResult.Success -> {
                        installedApps.add(installResult.app)
                    }
                    is ApkInstallationResult.Failure -> {
                        // handle gracefully, e.g. retry or skip this app
                    }
                }
            }
        }
    }

    internal suspend fun downloadAPK(app: App): DownloadResult {
        println(app)
        // add checks before calling downloadAPK like isNetworkAvailable, isStorageAvailable
        // perform download operation and logs download progress
        delay(1000) // simulate download operation
        return DownloadResult.Success(app)
    }

    internal suspend fun installApk(app: App): ApkInstallationResult {
        // register broadcast receiver for package added and package removed state
        val installationFlow: Flow<Boolean> = callbackFlow {
            // register receiver and send the result to the flow
            // if the app is installed, send true, otherwise false
            trySend(true).isSuccess // simulate installation result
        }
        val isInstalled = installationFlow.first()
        return if (isInstalled) {
            ApkInstallationResult.Success(app)
        } else {
            ApkInstallationResult.Failure(app, "Installation failed")
        }
    }
}
