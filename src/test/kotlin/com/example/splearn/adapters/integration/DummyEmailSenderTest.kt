package com.example.splearn.adapters.integration

import com.example.splearn.domain.shared.Email
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.StdIo
import org.junitpioneer.jupiter.StdOut
import kotlin.test.assertEquals

class DummyEmailSenderTest {
    @Test
    @StdIo
    fun send(out: StdOut) {
        val emailSender = DummyEmailSender()

        val emailAddress = "test@gmail.com"
        emailSender.send(
            email = Email.create(emailAddress),
            title = "title",
            content = "content"
        )

        assertEquals(
            "dummy email sent to $emailAddress",
            out.capturedLines()[0]
        )
    }
}