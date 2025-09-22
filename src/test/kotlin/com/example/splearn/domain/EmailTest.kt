package com.example.splearn.domain

import com.example.splearn.domain.shared.Email
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class EmailTest {
    @Test
    fun validEmail() {
        // GIVEN
        val validEmail = "hello@gmail.com"

        // WHEN & THEN
        assertDoesNotThrow { Email(validEmail) }
    }

    @Test
    fun invalidEmail() {
        // GIVEN
        val validEmails = listOf(
            "hello",
            "hello@google",
            "hello@google.",
            "hello#goolge.com",
        )

        validEmails.map {
            // WHEN & THEN
            assertThrows<Exception> { Email(it) }
        }
    }
}