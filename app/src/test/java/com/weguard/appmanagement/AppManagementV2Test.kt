package com.weguard.appmanagement

import com.weguard.appmanagement.utils.App
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppManagementV2Test {

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

    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    class AppManagementV2Test {

        private val apps = listOf(
            App("https:://www.example1.com", "111111111", 14354L, "com.example1.android", "Example1"),
            App("https:://www.example2.com", "222222222", 22434L, "com.example2.android", "Example2"),
            App("https:://www.example3.com", "333333333", 34355L, "com.example3.android", "Example3"),
            App("https:://www.example4.com", "444444444", 42343L, "com.example4.android", "Example4"),
            App("https:://www.example5.com", "555555555", 52343L, "com.example5.android", "Example5"),
            App("https:://www.example6.com", "666666666", 65445L, "com.example6.android", "Example6")
        )

        @Test
        fun `Test execute method with mocked appIsNotInstalled and installApp`() = runBlocking {
            val appManagementV2 = spyk(AppManagementV2(apps))

            // Mock the appIsNotInstalled method to return true or false randomly
            every { appManagementV2.appIsNotInstalled(any()) } returnsMany listOf(true, false, true, false, true, false)

            // Call the execute method
            appManagementV2.execute()

            // Verify that installApp is called only for the apps that return false by appIsNotInstalled
            coVerify(exactly = 1) { appManagementV2.installApp("com.example1.android") }
            coVerify(exactly = 1) { appManagementV2.installApp("com.example3.android") }
            coVerify(exactly = 1) { appManagementV2.installApp("com.example5.android") }
        }
    }


}