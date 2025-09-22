package com.example.splearn.domain

import com.example.splearn.domain.MemberFixture.Companion.DEFAULT_PASSWORD
import com.example.splearn.domain.MemberFixture.Companion.createMemberResiterRequest
import com.example.splearn.domain.MemberFixture.Companion.createPasswordEncoder
import com.example.splearn.domain.member.Member
import com.example.splearn.domain.member.MemberStatus
import com.example.splearn.domain.member.PasswordEncoder
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

    @BeforeEach
    fun setup() {
        passwordEncoder = createPasswordEncoder()
        member = Member.register(
            createMemberResiterRequest(),
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
        assertTrue { member.verifyPassword(DEFAULT_PASSWORD, passwordEncoder) }

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
        assertEquals(newNickname, member.nickname)
    }
}
