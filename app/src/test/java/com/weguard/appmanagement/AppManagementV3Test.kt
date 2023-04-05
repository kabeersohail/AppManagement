package com.weguard.appmanagement

import android.util.Log
import com.weguard.appmanagement.utils.App
import com.weguard.appmanagement.utils.AppDownloadResult
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppManagementV3Test {

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
    fun `execute should handle download success and failure`() = runBlocking {
        // Create mock objects
        val apps = listOf(
            App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
            App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
            App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
            App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4"),
            App("https:://www.example5.com", "555555555", 52343L, "com.example5.android", "Example5"),
            App("https:://www.example6.com", "666666666", 65445L, "com.example6.android", "Example6")
        )

        val appManagement = spyk(AppManagementV3(apps))

        // Mock the downloadApps function for success
        coEvery { appManagement.downloadApps(apps) } returns flow {
            emit(AppDownloadResult.Success(apps[0]))
            emit(AppDownloadResult.Success(apps[1]))
            emit(AppDownloadResult.Success(apps[2]))
            emit(AppDownloadResult.Failure(apps[3], "network issue"))
        }

        // Call the execute method
        appManagement.execute()

        // Verify that installApp is called for each successful app
        coVerify(exactly = 3) { appManagement.installApp(any()) }
        coVerify(exactly = 1) { appManagement.handleDownloadFailure(any()) }
    }

    @Test
    fun `execute should handle download success and failure another`() = runBlocking {
        // Create mock objects
        val apps = listOf(
            App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
            App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
            App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
            App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4"),
            App("https:://www.example5.com", "555555555", 52343L, "com.example5.android", "Example5"),
            App("https:://www.example6.com", "666666666", 65445L, "com.example6.android", "Example6")
        )

        val appManagement = spyk(AppManagementV3(apps))

        // Mock the downloadApps function
        coEvery { appManagement.downloadApps(apps) } returns flow {
            emit(AppDownloadResult.Success(apps[0]))
            emit(AppDownloadResult.Success(apps[1]))
            emit(AppDownloadResult.Success(apps[2]))
            emit(AppDownloadResult.Failure(apps[3], "Failed to download"))
            emit(AppDownloadResult.Failure(apps[4], "Failed to download"))
            emit(AppDownloadResult.Failure(apps[5], "Failed to download"))
        }

        // Call the execute method
        appManagement.execute()

        // Verify that installApp is called for each successful app
        coVerify(exactly = 3) { appManagement.installApp(any()) }

        // Verify that handleDownloadFailure is called for each failed app
        coVerify(exactly = 3) { appManagement.handleDownloadFailure(any()) }
    }


}