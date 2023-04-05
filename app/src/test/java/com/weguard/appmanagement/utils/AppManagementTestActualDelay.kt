package com.weguard.appmanagement.utils

import android.util.Log
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppManagementTestActualDelay {

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
    fun `test case with random delays with actual delay ranging from 1 to 10 seconds for both download and installation`() = runTest {
        val appManagement = spyk(AppManagement(apps))

        apps.forEach { app ->
            coEvery { appManagement.downloadAPK(app) } answers {
                val randomDelay = (1..10).random() * 1000L // Random delay between 1 to 10 seconds
                runBlocking { delay(randomDelay) }
                AppDownloadResult.Success(app)
            }
            coEvery { appManagement.installApk(app) } answers {
                val randomDelay = (1..10).random() * 1000L // Random delay between 1 to 10 seconds
                runBlocking { delay(randomDelay) }
                ApkInstallationResult.Success(app)
            }
        }

        val result = appManagement.execute()

        apps.forEach { app ->
            coVerifyOrder {
                appManagement.downloadAPK(app)
                appManagement.installApk(app)
            }
        }

        Assert.assertEquals(AppManagementResult.Success, result)
    }

}