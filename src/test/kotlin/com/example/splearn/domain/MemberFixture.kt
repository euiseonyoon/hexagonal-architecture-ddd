package com.example.splearn.domain

class MemberFixture {
    companion object {
        const val DEFAULT_PASSWORD = "secret"

        @JvmStatic
        fun createMemberResiterRequest() = MemberRegisterRequest(
            Email("test@gmail.com"),
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