package com.example.splearn.domain.shared

import java.util.regex.Pattern

@JvmRecord // <- 어노테이션을 사용하지 않으면 Email의 no arg 생성자가 없음. Jpa에서는 no arg 생성자가 필요하다.
data class Email(val address: String) {
    init {
        require(EMAIL_PATTERN.matcher(address).matches()) { "이메일 형식이 바르지 않습니다: $address" }
    }

    companion object {
        private val EMAIL_PATTERN: Pattern =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    }
}
