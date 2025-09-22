package com.example.splearn.domain.member

import java.util.regex.Pattern

class Profile private constructor() {
    lateinit var address: String
        protected set

    companion object {
        private val PROFILE_TITLE_PATTERN: Pattern = Pattern.compile("[a-z0-9]+")

        private fun validate(address: String) {
            require(PROFILE_TITLE_PATTERN.matcher(address).matches()) { "이메일 형식이 바르지 않습니다: $address" }
            require(address.length <= 15) { "프로필주소는 15자리까지 만 가능합니다." }
        }

        fun create(address: String): Profile {
            validate(address)
            return Profile().apply { this.address = address }
        }
    }
}
