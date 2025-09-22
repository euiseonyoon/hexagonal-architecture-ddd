package com.example.splearn.domain.shared

import java.util.regex.Pattern

class Email private constructor() {
    lateinit var address: String
        protected set

    companion object {
        private val EMAIL_PATTERN: Pattern =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")

        fun create(address: String): Email {
            require(EMAIL_PATTERN.matcher(address).matches()) { "이메일 형식이 바르지 않습니다: $address" }
            val email = Email()
            email.address = address
            return email
        }
    }
}
