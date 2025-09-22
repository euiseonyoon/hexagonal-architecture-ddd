package com.example.splearn.application.member.required

import com.example.splearn.domain.shared.Email
import com.example.splearn.domain.member.MemberFixture
import com.example.splearn.domain.member.PasswordEncoder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class SplearnTestConfig {
    @Bean
    fun emailSender(): EmailSender {
        return object : EmailSender {
            override fun send(email: Email, title: String, content: String) {
                println("email sent.")
            }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return MemberFixture.createPasswordEncoder()
    }
}