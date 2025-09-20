package com.example.splearn.domain

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MemberTest {
    lateinit var member: Member
    lateinit var passwordEncoder: PasswordEncoder

    val defaultPassword = "password"

    @BeforeEach
    fun setup() {
        passwordEncoder = object : PasswordEncoder {
            override fun encode(password: String): String {
                return password.uppercase()
            }

            override fun matches(password: String, passwordHash: String): Boolean {
                return passwordHash == this.encode(password)
            }
        }
        member = Member.register(
            MemberRegisterRequest(
                Email("test@gmail.com"),
                "Adam",
                defaultPassword,
            ),
            passwordEncoder
        )
    }

    @Test
    fun registerMember() {
        assertEquals(MemberStatus.PENDING, member.status)
    }

    @Test
    fun activateMember() {
        // GIVEN & WHEN
        assertDoesNotThrow { member.activate() }

        // THEN
        assertTrue { member.isActive() }
    }

    @Test
    fun deactivateMember() {
        // GIVEN
        assertDoesNotThrow { member.activate() }

        // WHEN
        member.deactivate()

        // THEN
        assertEquals(MemberStatus.DEACTIVATED, member.status)
    }

    @Test
    fun deactivateMemberInvalid() {
        assertThrows<Exception> { member.deactivate() }
    }

    @Test
    fun verifyPassword() {
        assertTrue { member.verifyPassword(defaultPassword, passwordEncoder) }

        assertFalse { member.verifyPassword("invalid password", passwordEncoder) }
    }

    @Test
    fun changePassword() {
        // GIVEN
        val newPassword = "someNewPassword"
        assertTrue { member.passwordHash != newPassword }

        // WHEN
        member.changePassword(newPassword, passwordEncoder)

        // THEN
        assertEquals(passwordEncoder.encode(newPassword), member.passwordHash)
    }

    @Test
    fun changeNickname() {
        // GIVEN
        val newNickname = "foo"

        // WHEN
        member.changeNickname(newNickname)

        // THEN
        assertEquals(newNickname, member.nickName)
    }
}
