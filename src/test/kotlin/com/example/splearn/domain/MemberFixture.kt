package com.example.splearn.domain

import com.example.splearn.domain.member.MemberRegisterRequest
import com.example.splearn.domain.member.PasswordEncoder

class MemberFixture {
    companion object {
        const val DEFAULT_PASSWORD = "secret"

        @JvmStatic
        fun createMemberRegisterRequest() = MemberRegisterRequest(
            "test@gmail.com",
            "Adam",
            DEFAULT_PASSWORD,
        )

        @JvmStatic
        fun createPasswordEncoder() = object : PasswordEncoder {
            override fun encode(password: String): String {
                return password.uppercase()
            }

            override fun matches(password: String, passwordHash: String): Boolean {
                return passwordHash == this.encode(password)
            }
        }
    }
}