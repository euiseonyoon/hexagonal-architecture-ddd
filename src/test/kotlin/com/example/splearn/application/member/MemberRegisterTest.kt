package com.example.splearn.application.member

import com.example.splearn.TestUtils.Companion.generateRandomString
import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.application.member.required.SplearnTestConfig
import com.example.splearn.domain.member.MemberFixture
import com.example.splearn.domain.member.MemberFixture.Companion.DEFAULT_PASSWORD
import com.example.splearn.domain.member.MemberFixture.Companion.createMemberInfoUpdateRequest
import com.example.splearn.domain.member.*
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
@Import(SplearnTestConfig::class)
// 아래 코드를 사용해도 되지만, junit-platform.properties에 설정해두었음.
// @TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class MemberRegisterTest(
    private val memberRegister: MemberRegister,
    private val entityManager: EntityManager,
) {
    @Test
    fun register() {
        val member = memberRegister.register(MemberFixture.createMemberRegisterRequest())

        assertNotNull(member.id)
        assertEquals(MemberStatus.PENDING, member.status)
        assertNotNull(member.detail)
    }

    @Test
    fun duplicateEmail() {
        memberRegister.register(MemberFixture.createMemberRegisterRequest())
        assertThrows<DuplicateEmailException> {
            memberRegister.register(MemberFixture.createMemberRegisterRequest())
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
        val member = memberRegister.register(MemberFixture.createMemberRegisterRequest())
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
        assertThrows<IllegalArgumentException> {
            memberRegister.activate(member.id!!)
        }.also { assertEquals("Pending 상태가 아닙니다.", it.message) }
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
        assertThrows<IllegalArgumentException> {
            memberRegister.deactivate(member.id!!)
        }.also { assertEquals("Active 상태가 아닙니다.", it.message!!) }
    }

    @Test
    fun updateInfo() {
        // GIVEN
        val newProfileAddress = "address123"
        val member = registerMember().also { memberRegister.activate(it.id!!) }

        entityManager.flush()
        entityManager.clear()

        // WHEN
        val updateRequest = createMemberInfoUpdateRequest(newProfileAddress)
        val updatedMember = memberRegister.updateInfo(member.id!!, updateRequest)

        // THEN
        assertEquals(updateRequest.nickname, updatedMember.nickname)
        assertEquals(updateRequest.profileAddress, updatedMember.detail.profile?.address)
        assertEquals(updateRequest.introduction, updatedMember.detail.introduction)
    }

    @Test
    fun `updateInfoFail Not Active`() {
        // GIVEN
        val member = registerMember()
        assertNotNull(member.id)

        entityManager.flush()
        entityManager.clear()

        // WHEN & THEN
        assertThrows<IllegalArgumentException> {
            memberRegister.updateInfo(member.id!!, createMemberInfoUpdateRequest("newprofile"))
        }.also { assertEquals("Active 상태가 아닙니다.", it.message) }
    }

    @Test
    fun `updateInfoFail Duplicate profile address`() {
        // GIVEN
        val duplicateProfileAddress = "address123"

        val member = registerMember().also { memberRegister.activate(it.id!!) }
        val member2 = registerMember("blake@gmail.com").also { memberRegister.activate(it.id!!) }

        memberRegister.updateInfo(member.id!!, createMemberInfoUpdateRequest(duplicateProfileAddress))

        entityManager.flush()
        entityManager.clear()

        // WHEN & THEN
        assertThrows<DuplicateProfileAddressException> {
            memberRegister.updateInfo(member2.id!!, createMemberInfoUpdateRequest(duplicateProfileAddress))
        }.also {
            assertEquals("이미 존재하는 프로필 주소입니다. profile_address : {$duplicateProfileAddress}", it.message)
        }

        // WHEN & THEN : 회원이 본인이 원래 가지고 있던 profile_address를 그대로 사용하여 수정하는 경우
        assertDoesNotThrow {
            memberRegister.updateInfo(member.id!!, createMemberInfoUpdateRequest(duplicateProfileAddress))
        }

        // WHEN & THEN : 비어있는 profile 이름으로 수정 (profile address 수정되지 않음)
        assertDoesNotThrow {
            memberRegister.updateInfo(member.id!!, createMemberInfoUpdateRequest(""))
        }

        // WHEN & THEN : null인 profile 이름으로 수정 (profile address 수정되지 않음)
        assertDoesNotThrow {
            memberRegister.updateInfo(member.id!!, createMemberInfoUpdateRequest(null))
        }
    }
}
