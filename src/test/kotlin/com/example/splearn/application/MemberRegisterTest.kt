package com.example.splearn.application

import com.example.splearn.TestUtils.Companion.generateRandomString
import com.example.splearn.application.provided.MemberRegister
import com.example.splearn.application.required.SplearnTestConfig
import com.example.splearn.domain.DuplicateEmailException
import com.example.splearn.domain.MemberFixture
import com.example.splearn.domain.MemberRegisterRequest
import com.example.splearn.domain.MemberStatus
import jakarta.transaction.Transactional
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest
@Transactional
@Import(SplearnTestConfig::class)
// @TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class MemberRegisterTest(
    private val memberRegister: MemberRegister
) {
    @Test
    fun register() {
        val member = memberRegister.register(MemberFixture.createMemberResiterRequest())

        assertNotEquals(0L, member.id)
        assertEquals(MemberStatus.PENDING, member.status)
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
            memberRegister.register(MemberRegisterRequest(
                email = "test@gmail.com",
                nickname = "Tony",
                password = generateRandomString(1)
            ))
        }

        assertThrows<ConstraintViolationException> {
            memberRegister.register(MemberRegisterRequest(
                email = "test@gmail.com",
                nickname = "Tony",
                password = generateRandomString(25)
            ))
        }
    }

    @Test
    fun registerFailInvalidNicknameSize() {
        assertThrows<ConstraintViolationException> {
            memberRegister.register(MemberRegisterRequest(
                email = "test@gmail.com",
                nickname = generateRandomString(1),
                password = "12345"
            ))
        }

        assertThrows<ConstraintViolationException> {
            memberRegister.register(MemberRegisterRequest(
                email = "test@gmail.com",
                nickname = generateRandomString(25),
                password = "12345"
            ))
        }
    }

    @Test
    fun registerFailInvalidEmailAddress() {
        assertThrows<ConstraintViolationException> {
            memberRegister.register(MemberRegisterRequest(
                email = "test",
                nickname = "Adam",
                password = "12345"
            ))
        }

        assertThrows<ConstraintViolationException> {
            memberRegister.register(MemberRegisterRequest(
                email = "test@",
                nickname = "Adam",
                password = "12345"
            ))
        }
    }

    @Test
    fun activate() {
        // GIVEN
        val member = memberRegister.register(MemberFixture.createMemberResiterRequest())

        // WHEN
        member.activate()

        // THEN
        assertEquals(MemberStatus.ACTIVE, member.status)
    }
}

