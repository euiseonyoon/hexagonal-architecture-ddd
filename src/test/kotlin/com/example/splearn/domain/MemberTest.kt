package com.example.splearn.domain

import com.example.splearn.domain.MemberFixture.Companion.DEFAULT_PASSWORD
import com.example.splearn.domain.MemberFixture.Companion.createMemberRegisterRequest
import com.example.splearn.domain.MemberFixture.Companion.createPasswordEncoder
import com.example.splearn.domain.member.Member
import com.example.splearn.domain.member.MemberInfoUpdateRequest
import com.example.splearn.domain.member.MemberStatus
import com.example.splearn.domain.member.PasswordEncoder
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
            createMemberRegisterRequest(),
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
        assertNotNull { member.detail.activatedAt }
    }

    @Test
    fun deactivateMember() {
        // GIVEN
        assertDoesNotThrow { member.activate() }

        // WHEN
        member.deactivate()

        // THEN
        assertEquals(MemberStatus.DEACTIVATED, member.status)
        assertNotNull { member.detail.deactivatedAt }
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

    @ParameterizedTest
    @CsvSource(
        "foo, null, null",
        "foo, null, 'hello there'",
        "foo, '123456', null",
        "foo, '123456', 'hello there'"
    )
    fun updateInfo(
        newNickname: String,
        profileAddress: String?,
        introduction: String?
    ) {
        // GIVEN
        val memberInfoUpdateRequest = MemberInfoUpdateRequest(
            nickname = newNickname,
            profileAddress = profileAddress,
            introduction = introduction,
        )
        member.activate()

        // WHEN
        member.updateInfo(memberInfoUpdateRequest)

        // THEN
        assertEquals(newNickname, member.nickname)
        assertEquals(profileAddress, member.detail.profile?.address)
        assertEquals(introduction, member.detail.introduction)
    }
}
