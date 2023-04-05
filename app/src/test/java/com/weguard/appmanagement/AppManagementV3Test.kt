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

    // Create mock objects
    private val apps = listOf(
        App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
        App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
        App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
        App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4"),
        App("https:://www.example5.com", "555555555", 52343L, "com.example5.android", "Example5"),
        App("https:://www.example6.com", "666666666", 65445L, "com.example6.android", "Example6")
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
    fun `when apps list is empty neither download apps, install apps and handleDownloadFailure must not be called`() = runBlocking {
        // Given
        val appsList = emptyList<App>()
        val appManagement = spyk(AppManagementV3(appsList))

        appManagement.execute()

        // Then
        coVerify(exactly = 0) { appManagement.downloadApps(appsList) }
        coVerify(exactly = 0) { appManagement.installApp(any()) }
        coVerify(exactly = 0) { appManagement.handleDownloadFailure(any()) }
    }

    @Test
    fun `when download apps emit success for single app, then install app must be called`() = runBlocking {
        // Given
        val app = App(
            "https:://www.example1.com",
            "111111111",
            14354L,
            "com.example1.android",
            "Example1"
        )

        val appsList = listOf(app)
        val appManagement = spyk(AppManagementV3(appsList))

        // When
        coEvery { appManagement.downloadApps(appsList) } returns flow {
            emit(AppDownloadResult.Success(appsList.first()))
        }

        appManagement.execute()

        // Then
        coVerify(exactly = 1) { appManagement.installApp(app.packageName) }
    }

    @Test
    fun `when download apps emit success for single app, then handleDownloadFailure must not be called`() = runBlocking {
        // Given
        val app = App(
            "https:://www.example1.com",
            "111111111",
            14354L,
            "com.example1.android",
            "Example1"
        )

        val appsList = listOf(app)
        val appManagement = spyk(AppManagementV3(appsList))

        // When
        coEvery { appManagement.downloadApps(appsList) } returns flow {
            emit(AppDownloadResult.Success(appsList.first()))
        }

        appManagement.execute()

        // Then
        coVerify(exactly = 0) { appManagement.handleDownloadFailure(app) }
    }

    @Test
    fun `when download apps emit success for all apps, then install app must be called for all apps`() = runBlocking {
        // Given
        val apps = listOf(
            App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
            App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
            App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
            App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4"),
            App("https:://www.example5.com", "555555555", 52343L, "com.example5.android", "Example5"),
            App("https:://www.example6.com", "666666666", 65445L, "com.example6.android", "Example6")
        )

        val appManagement = spyk(AppManagementV3(apps))

        // When
        coEvery { appManagement.downloadApps(apps) } returns flow {
            apps.forEach { emit(AppDownloadResult.Success(it)) }
        }

        appManagement.execute()

        // Then
        apps.forEach {
            coVerify(exactly = 1) { appManagement.installApp(it.packageName) }
        }
    }

    @Test
    fun `when download apps emit failed for all apps, then handleDownloadFailure must be called for all apps`() = runBlocking {
        // Given
        val apps = listOf(
            App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
            App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
            App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
            App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4"),
            App("https:://www.example5.com", "555555555", 52343L, "com.example5.android", "Example5"),
            App("https:://www.example6.com", "666666666", 65445L, "com.example6.android", "Example6")
        )

        val appManagement = spyk(AppManagementV3(apps))

        // When
        coEvery { appManagement.downloadApps(apps) } returns flow {
            apps.forEach { emit(AppDownloadResult.Failure(it, "Network issue")) }
        }

        appManagement.execute()

        // Then
        apps.forEach {
            coVerify(exactly = 1) { appManagement.handleDownloadFailure(it) }
        }
    }

    @Test
    fun `when download apps emit success for some apps and failure for some apps, then install app and handle download failure must be called accordingly`() = runBlocking {
        // Given
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
            emit(AppDownloadResult.Failure(apps[1], "Failed to download"))
            emit(AppDownloadResult.Success(apps[2]))
            emit(AppDownloadResult.Failure(apps[3], "Failed to download"))
            emit(AppDownloadResult.Success(apps[4]))
            emit(AppDownloadResult.Failure(apps[5], "Failed to download"))
        }

        // When
        appManagement.execute()

        // Then
        coVerify(exactly = 1) { appManagement.installApp(apps[0].packageName) }
        coVerify(exactly = 0) { appManagement.handleDownloadFailure(apps[0]) }

        coVerify(exactly = 1) { appManagement.handleDownloadFailure(apps[1]) }
        coVerify(exactly = 0) { appManagement.installApp(apps[1].packageName) }

        coVerify(exactly = 1) { appManagement.installApp(apps[2].packageName) }
        coVerify(exactly = 0) { appManagement.handleDownloadFailure(apps[2]) }

        coVerify(exactly = 1) { appManagement.handleDownloadFailure(apps[3]) }
        coVerify(exactly = 0) { appManagement.installApp(apps[3].packageName) }

        coVerify(exactly = 1) { appManagement.installApp(apps[4].packageName) }
        coVerify(exactly = 0) { appManagement.handleDownloadFailure(apps[4]) }

        coVerify(exactly = 1) { appManagement.handleDownloadFailure(apps[5]) }
        coVerify(exactly = 0) { appManagement.installApp(apps[5].packageName) }
    }

    @Test
    fun `when download apps emit success for all apps, then install app must be called sequentially`() = runBlocking {
        // Given
        val apps = listOf(
            App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
            App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
            App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
            App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4")
        )

        val appManagement = spyk(AppManagementV3(apps))

        // Mock the downloadApps function
        coEvery { appManagement.downloadApps(apps) } returns flow {
            emit(AppDownloadResult.Success(apps[0]))
            emit(AppDownloadResult.Success(apps[1]))
            emit(AppDownloadResult.Success(apps[2]))
            emit(AppDownloadResult.Success(apps[3]))
        }

        // When
        appManagement.execute()

        // Then
        coVerifyOrder {
            appManagement.installApp(apps[0].packageName)
            appManagement.installApp(apps[1].packageName)
            appManagement.installApp(apps[2].packageName)
            appManagement.installApp(apps[3].packageName)
        }
    }

    @Test
    fun `when download apps emit failure for all apps, then handleDownloadFailure must be called sequentially`() = runBlocking {
        // Given
        val apps = listOf(
            App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
            App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
            App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
            App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4")
        )

        val appManagement = spyk(AppManagementV3(apps))

        // Mock the downloadApps function
        coEvery { appManagement.downloadApps(apps) } returns flow {
            emit(AppDownloadResult.Failure(apps[0], "Network issue"))
            emit(AppDownloadResult.Failure(apps[1], "Network issue"))
            emit(AppDownloadResult.Failure(apps[2], "Network issue"))
            emit(AppDownloadResult.Failure(apps[3], "Network issue"))
        }

        // When
        appManagement.execute()

        // Then
        coVerifyOrder {
            appManagement.handleDownloadFailure(apps[0])
            appManagement.handleDownloadFailure(apps[1])
            appManagement.handleDownloadFailure(apps[2])
            appManagement.handleDownloadFailure(apps[3])
        }
    }

}