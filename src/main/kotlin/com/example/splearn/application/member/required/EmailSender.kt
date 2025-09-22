package com.example.splearn.application.member.required

import com.example.splearn.domain.shared.Email

/**
 * Email을 발송한다.
 * */
interface EmailSender {
    fun send(email: Email, title: String, content: String)
}
