package com.example.splearn.domain.member

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

        @JvmStatic
        fun createMemberInfoUpdateRequest(profileAddress: String?) = MemberInfoUpdateRequest(
            "Smith",
            profileAddress,
            "this is introduction."
        )
    }
}