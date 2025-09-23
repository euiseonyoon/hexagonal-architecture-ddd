package com.example.splearn.domain.member

import com.example.splearn.domain.member.Member.Companion.register
import org.springframework.test.util.ReflectionTestUtils

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
        fun createMemberRegisterRequest(email: String) = MemberRegisterRequest(
            email,
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

        @JvmStatic
        fun createMember(id: Long): Member {
            val member = register(createMemberRegisterRequest(), createPasswordEncoder())
            ReflectionTestUtils.setField(member, "id", id)
            return member
        }
    }
}