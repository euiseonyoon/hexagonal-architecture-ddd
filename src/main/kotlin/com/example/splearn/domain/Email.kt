package com.example.splearn.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Email(
    @Column(name = "email_address", length = 150, nullable = false)
    val address: String
) {
    init {
        val emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        require(emailRegex.matches(address)) {
            "올바른 이메일 형식이 아닙니다."
        }
    }
}
