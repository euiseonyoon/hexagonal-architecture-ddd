package com.example.splearn.application

import com.example.splearn.TestUtils.Companion.generateRandomString
import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.application.required.SplearnTestConfig
import com.example.splearn.domain.MemberFixture
import com.example.splearn.domain.MemberFixture.Companion.DEFAULT_PASSWORD
import com.example.splearn.domain.member.*
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest
@Transactional
@Import(SplearnTestConfig::class)
// @TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class MemberRegisterTest(
    private val memberRegister: MemberRegister,
    private val entityManager: EntityManager,
) {
    @Test
    fun register() {
        val member = memberRegister.register(MemberFixture.createMemberResiterRequest())

        assertNotEquals(0L, member.id)
        assertEquals(MemberStatus.PENDING, member.status)
        assertNotNull(member.detail)
    }

    @Test
    fun duplicateEmail() {
        memberRegister.register(MemberFixture.createMemberResiterRequest())
        assertThrows<DuplicateEmailException> {
            memberRegister.register(MemberFixture.createMemberResiterRequest())
        }
    }

    @Test
    fun registerFailInvalidPasswordSize() {
        assertThrows<ConstraintViolationException> {
            memberRegister.register(
                MemberRegisterRequest(
                    email = "test@gmail.com",
                    nickname = "Tony",
                    password = generateRandomString(1)
                )
            )
        }

        assertThrows<ConstraintViolationException> {
            memberRegister.register(
                MemberRegisterRequest(
                    email = "test@gmail.com",
                    nickname = "Tony",
                    password = generateRandomString(25)
                )
            )
        }
    }

    @Test
    fun registerFailInvalidNicknameSize() {
        assertThrows<ConstraintViolationException> {
            memberRegister.register(
                MemberRegisterRequest(
                    email = "test@gmail.com",
                    nickname = generateRandomString(1),
                    password = "12345"
                )
            )
        }

        assertThrows<ConstraintViolationException> {
            memberRegister.register(
                MemberRegisterRequest(
                    email = "test@gmail.com",
                    nickname = generateRandomString(25),
                    password = "12345"
                )
            )
        }
    }

    @Test
    fun registerFailInvalidEmailAddress() {
        assertThrows<ConstraintViolationException> {
            memberRegister.register(
                MemberRegisterRequest(
                    email = "test",
                    nickname = "Adam",
                    password = "12345"
                )
            )
        }

        assertThrows<ConstraintViolationException> {
            memberRegister.register(
                MemberRegisterRequest(
                    email = "test@",
                    nickname = "Adam",
                    password = "12345"
                )
            )
        }
    }

    private fun registerMember(): Member {
        val member = memberRegister.register(MemberFixture.createMemberResiterRequest())
        entityManager.flush()
        return member
    }

    private fun registerMember(email: String): Member {
        val member = memberRegister.register(
            MemberRegisterRequest(
                email,
                "Blake",
                DEFAULT_PASSWORD,
            )
        )
        entityManager.flush()
        return member
    }

    @Test
    fun activate() {
        // GIVEN
        val member = registerMember()
        assertNotNull(member.id)
        assertNull(member.detail.activatedAt)

        // WHEN
        memberRegister.activate(member.id!!)
        entityManager.flush()

        // THEN
        assertEquals(MemberStatus.ACTIVE, member.status)
        assertNotNull(member.detail.activatedAt)

        // WHEN & THEN
        val result = assertThrows<IllegalArgumentException> {
            memberRegister.activate(member.id!!)
        }
        assertNotNull(result.message)
        assertEquals("Pending 상태가 아닙니다.", result.message!!)
    }

    @Test
    fun deactivate() {
        // GIVEN
        val member = registerMember()
        assertNotNull(member.id)
        memberRegister.activate(member.id!!)

        // WHEN
        memberRegister.deactivate(member.id!!)
        entityManager.flush()
        entityManager.clear()

        // THEN
        assertEquals(MemberStatus.DEACTIVATED, member.status)
        assertNotNull(member.detail.deactivatedAt)
    }

    @Test
    fun deactivateFail() {
        // GIVEN
        val member = registerMember()
        assertNotNull(member.id)
        entityManager.flush()
        entityManager.clear()

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            memberRegister.deactivate(member.id!!)
        }
        assertEquals("Active 상태가 아닙니다.", exception.message!!)
    }

    @Test
    fun updateInfo() {
        // GIVEN
        val newNickname = "Smith"
        val newProfileAddress = "address123"
        val newIntroduction = "hello there"

        val member = registerMember()
        assertNotNull(member.id)
        memberRegister.activate(member.id!!)

        entityManager.flush()
        entityManager.clear()

        // WHEN
        val updatedMember = memberRegister.updateInfo(
            member.id!!,
            MemberInfoUpdateRequest(
                newNickname,
                newProfileAddress,
                newIntroduction
            )
        )

        // THEN
        assertEquals(newNickname, updatedMember.nickname)
        assertEquals(newProfileAddress, updatedMember.detail.profile?.address)
        assertEquals(newIntroduction, updatedMember.detail.introduction)
    }

    @Test
    fun `updateInfoFail Not Active`() {
        // GIVEN
        val newNickname = "Smith"
        val newProfileAddress = "address123"
        val newIntroduction = "hello there"

        val member = registerMember()
        assertNotNull(member.id)
        entityManager.flush()
        entityManager.clear()

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            memberRegister.updateInfo(
                member.id!!,
                MemberInfoUpdateRequest(
                    newNickname,
                    newProfileAddress,
                    newIntroduction
                )
            )
        }
        assertEquals("Active 상태가 아닙니다.", exception.message)
    }

    @Test
    fun `updateInfoFail Duplicate profile address`() {
        // GIVEN
        val newNickname = "Smith"
        val duplicateProfileAddress = "address123"
        val newIntroduction = "hello there"

        val member = registerMember()
        val member2 = registerMember("blake@gmail.com")

        memberRegister.activate(member.id!!)
        memberRegister.activate(member2.id!!)

        entityManager.flush()
        entityManager.clear()

        memberRegister.updateInfo(
            member.id!!,
            MemberInfoUpdateRequest(
                newNickname,
                duplicateProfileAddress,
                newIntroduction
            )
        )

        // WHEN & THEN
        val exception = assertThrows<DuplicateProfileAddressException> {
            memberRegister.updateInfo(
                member2.id!!,
                MemberInfoUpdateRequest(
                    "NewNickname",
                    duplicateProfileAddress,
                    "hello there"
                )
            )
        }
        assertEquals(
            "이미 존재하는 프로필 주소입니다. profile_address : {$duplicateProfileAddress}",
            exception.message
        )

        // WHEN & THEN : 회원이 본인이 원래 가지고 있던 profile_address를 그대로 사용하여 수정하는 경우
        assertDoesNotThrow {
            memberRegister.updateInfo(
                member.id!!,
                MemberInfoUpdateRequest(
                    "NewNickname",
                    duplicateProfileAddress,
                    "hello there"
                )
            )
        }

        // WHEN & THEN : 비어있는 profile 이름으로 수정 (profile address 수정되지 않음)
        assertDoesNotThrow {
            memberRegister.updateInfo(
                member.id!!,
                MemberInfoUpdateRequest(
                    "NewNickname",
                    "",
                    "hello there"
                )
            )
        }

        // WHEN & THEN : null인 profile 이름으로 수정 (profile address 수정되지 않음)
        assertDoesNotThrow {
            memberRegister.updateInfo(
                member.id!!,
                MemberInfoUpdateRequest(
                    "NewNickname",
                    null,
                    "hello there"
                )
            )
        }
    }
}
