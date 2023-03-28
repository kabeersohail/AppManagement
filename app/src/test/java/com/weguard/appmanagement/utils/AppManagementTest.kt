package com.weguard.appmanagement.utils

import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AppManagementTest {

    @ExperimentalCoroutinesApi
    @Test
    fun `test execute function with successful download and installation`() {
        // Arrange
        val apps = listOf(
            App(
                "http://example.com/app1",
                "123456",
                100L,
                "com.example.app1",
                "App 1"
            ),
            App(
                "http://example.com/app2",
                "234567",
                200L,
                "com.example.app2",
                "App 2"
            ),
            App(
                "http://example.com/app3",
                "345678",
                300L,
                "com.example.app3",
                "App 3"
            )
        )
        val appManagement = spyk(AppManagement(apps))
        val downloadResult = DownloadResult.Success(apps[0])
        val installResult = ApkInstallationResult.Success(apps[0])
        val mockDownloadTime = 500L
        val mockInstallTime = 200L

        // Mock
        coEvery { appManagement.downloadAPK(any()) } returns downloadResult
        coEvery { appManagement.installApk(any()) } returns installResult

        // Act
        val result = runBlocking { appManagement.execute() }

        // Assert
        assertEquals(AppManagementResult.Success, result)

        // Verify
        for (app in apps) {
            coEvery { appManagement.downloadAPK(app) } coAnswers {
                delay(mockDownloadTime)
                downloadResult
            }
            coEvery { appManagement.installApk(app) } coAnswers {
                delay(mockInstallTime)
                installResult
            }
        }
        for (app in apps) {
            coEvery { appManagement.downloadAPK(app) }
            coEvery { appManagement.installApk(app) }
        }
    }
}
