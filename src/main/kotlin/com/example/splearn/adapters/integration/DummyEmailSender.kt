package com.example.splearn.adapters.integration

import com.example.splearn.application.member.required.EmailSender
import com.example.splearn.domain.shared.Email
import org.springframework.stereotype.Component

@Component
class DummyEmailSender : EmailSender {
    override fun send(email: Email, title: String, content: String) {
        println("dummy email sent to ${email.address}")
    }
}
