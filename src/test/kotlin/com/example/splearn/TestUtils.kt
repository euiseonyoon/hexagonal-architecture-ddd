package com.example.splearn

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
    }
}
