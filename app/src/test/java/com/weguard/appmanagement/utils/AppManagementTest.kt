package com.weguard.appmanagement.utils

import android.util.Log
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppManagementTest {

    private val apps = listOf(
        App("https:://www.example1.com", "111111111", 1L, "com.example1.android", "Example1"),
        App("https:://www.example2.com", "222222222", 2L, "com.example2.android", "Example2"),
        App("https:://www.example3.com", "333333333", 3L, "com.example3.android", "Example3"),
        App("https:://www.example4.com", "444444444", 4L, "com.example4.android", "Example4"),
        App("https:://www.example5.com", "555555555", 5L, "com.example5.android", "Example5"),
        App("https:://www.example6.com", "666666666", 6L, "com.example6.android", "Example6")
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }



    @Test
    fun `first example test for single app`(): Unit = runBlocking {

        val mySampleApp = apps.first()

        val appManagement = spyk(AppManagement(listOf(mySampleApp)))

        coEvery { appManagement.downloadAPK(mySampleApp) } returns DownloadResult.Success(mySampleApp)
        coEvery { appManagement.installApk(mySampleApp) } returns ApkInstallationResult.Success(mySampleApp)

        val result = appManagement.execute()

        coVerify(exactly = 1) { appManagement.downloadAPK(mySampleApp) }

        assertTrue(appManagement.downloadedApps.contains(mySampleApp))
        assertEquals(1, appManagement.downloadedApps.size)

        coVerify(exactly = 1) { appManagement.installApk(mySampleApp) }

        assertEquals(AppManagementResult.Success, result)
    }

    @Test
    fun `first example test for multiple apps`(): Unit = runBlocking {

        val appManagement = spyk(AppManagement(apps))

        apps.forEach { app ->
            coEvery { appManagement.downloadAPK(app) } returns DownloadResult.Success(app)
            coEvery { appManagement.installApk(app) } returns ApkInstallationResult.Success(app)
        }

        val result = appManagement.execute()

        coVerify(exactly = 6) { appManagement.downloadAPK(any()) }
        coVerify(exactly = 6) { appManagement.installApk(any()) }
        assertTrue(appManagement.downloadedApps.containsAll(apps))

        assertEquals(AppManagementResult.Success, result)
    }

    @Test
    fun `first test case to verify order for single app`() = runBlocking {

        val mySampleApp = apps.first()
        val appManagement = spyk(AppManagement(listOf(mySampleApp)))

        coEvery { appManagement.downloadAPK(mySampleApp) } returns DownloadResult.Success(mySampleApp)
        coEvery { appManagement.installApk(mySampleApp) } returns ApkInstallationResult.Success(mySampleApp)

        val result = appManagement.execute()

        coVerify(exactly = 1) { appManagement.downloadAPK(mySampleApp) }

        assertTrue(appManagement.downloadedApps.contains(mySampleApp))
        assertEquals(1, appManagement.downloadedApps.size)

        coVerify(exactly = 1) { appManagement.installApk(mySampleApp) }

        assertEquals(AppManagementResult.Success, result)
    }


}
