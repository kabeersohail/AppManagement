package com.weguard.appmanagement.utils

import android.util.Log
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
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
    fun `first example test for single app`(): Unit = runTest {

        // Given
        val mySampleApp = apps.first()

        val appManagement = spyk(AppManagement(listOf(mySampleApp)))

        coEvery { appManagement.downloadAPK(mySampleApp) } returns DownloadResult.Success(mySampleApp)
        coEvery { appManagement.installApk(mySampleApp) } returns ApkInstallationResult.Success(mySampleApp)

        // When
        val result = appManagement.execute()

        // Then
        coVerify(exactly = 1) { appManagement.downloadAPK(mySampleApp) }

        assertTrue(appManagement.downloadedApps.contains(mySampleApp))
        assertEquals(1, appManagement.downloadedApps.size)

        coVerify(exactly = 1) { appManagement.installApk(mySampleApp) }

        assertEquals(AppManagementResult.Success, result)
    }

    @Test
    fun `first example test for multiple apps`(): Unit = runTest {

        // Given
        val appManagement = spyk(AppManagement(apps))

        apps.forEach { app ->
            coEvery { appManagement.downloadAPK(app) } returns DownloadResult.Success(app)
            coEvery { appManagement.installApk(app) } returns ApkInstallationResult.Success(app)
        }

        // When
        val result = appManagement.execute()

        // Then
        coVerify(exactly = 6) { appManagement.downloadAPK(any()) }
        coVerify(exactly = 6) { appManagement.installApk(any()) }
        assertTrue(appManagement.downloadedApps.containsAll(apps))

        assertEquals(AppManagementResult.Success, result)
    }

    @Test
    fun `first test case to verify order for single app`() = runBlocking {

        // Given
        val mySampleApp = apps.first()
        val appManagement = spyk(AppManagement(listOf(mySampleApp)))

        coEvery { appManagement.downloadAPK(mySampleApp) } returns DownloadResult.Success(mySampleApp)
        coEvery { appManagement.installApk(mySampleApp) } returns ApkInstallationResult.Success(mySampleApp)

        // When
        val result = appManagement.execute()

        // Then

        coVerifyOrder {
            appManagement.downloadAPK(mySampleApp)
            appManagement.installApk(mySampleApp)
        }

        assertEquals(AppManagementResult.Success, result)

    }

    @Test
    fun `first test case to verify order for multiple apps`() = runTest {

        // Given
        val appManagement = spyk(AppManagement(apps))

        apps.forEach { app ->
            coEvery { appManagement.downloadAPK(app) } returns DownloadResult.Success(app)
            coEvery { appManagement.installApk(app) } returns ApkInstallationResult.Success(app)
        }

        // When
        val result = appManagement.execute()

        // Then
        coVerifyOrder {
            apps.forEach { app ->
                appManagement.downloadAPK(app)
                appManagement.installApk(app)
            }
        }

        assertEquals(AppManagementResult.Success, result)
    }


    @Test
    fun `test case with random delays, with no actual delay`() = runTest {

        // Given
        val appManagement = spyk(AppManagement(apps))

        apps.forEach { app ->
            coEvery { appManagement.downloadAPK(app) } answers {
                val randomDelay = (5..180).random() * 1000L // Random delay between 5 seconds to 3 minutes
                advanceTimeBy(randomDelay)
                DownloadResult.Success(app)
            }
            coEvery { appManagement.installApk(app) } answers {
                val randomDelay = (2..60).random() * 1000L // Random delay between 2 seconds to 1 minute
                advanceTimeBy(randomDelay)
                ApkInstallationResult.Success(app)
            }
        }

        // When
        val result = appManagement.execute()

        // Then
        apps.forEach { app ->
            coVerifyOrder {
                appManagement.downloadAPK(app)
                appManagement.installApk(app)
            }
        }

        assertEquals(AppManagementResult.Success, result)
    }

    @Test
    fun `verify that installApps was called for each app`() = runTest {

        // Given
        val appManagement = spyk(AppManagement(apps))

        apps.forEach { app ->
            coEvery { appManagement.downloadAPK(app) } answers {
                val randomDelay = (5..180).random() * 1000L // Random delay between 5 seconds to 3 minutes
                advanceTimeBy(randomDelay)
                DownloadResult.Success(app)
            }
            coEvery { appManagement.installApk(app) } answers {
                val randomDelay = (2..60).random() * 1000L // Random delay between 2 seconds to 1 minute
                advanceTimeBy(randomDelay)
                ApkInstallationResult.Success(app)
            }
        }

        // When
        val result = appManagement.execute()

        // Then
        coVerify(exactly = apps.size) { appManagement.installApps(any(), any()) }

        assertEquals(AppManagementResult.Success, result)
    }



}
