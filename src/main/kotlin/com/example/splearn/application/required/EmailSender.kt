package com.example.splearn.application.required

import com.example.splearn.domain.Email

/**
 * Email을 발송한다.
 * */
interface EmailSender {
    fun send(email: Email, title: String, content: String)
}
