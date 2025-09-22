package com.example.splearn.adapters.security

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class SecurePasswordEncoderTest {
    @Test
    fun securePasswordEncoder() {
        // GIVEN
        val password = "secret"
        val securePasswordEncoder = SecurePasswordEncoder()

        // WHEN
        val passwordHash = securePasswordEncoder.encode(password)

        // THEN
        assertTrue { securePasswordEncoder.matches(password, passwordHash) }
    }
}