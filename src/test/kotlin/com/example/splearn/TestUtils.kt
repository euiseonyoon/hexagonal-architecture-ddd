package com.example.splearn

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult
import kotlin.random.Random

class TestUtils {
    companion object {
        @JvmStatic
        fun generateRandomString(length: Int): String {
            val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val randomString = StringBuilder(length)

            for (i in 0 until length) {
                randomString.append(charPool[Random.nextInt(charPool.size)])
            }

            return randomString.toString()
        }

        @JvmStatic
        fun makePostCall(mockMvcTester: MockMvcTester, uri: String, requestJson: String): MvcTestResult {
            return mockMvcTester.post().uri(uri).contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange()
        }
    }
}
