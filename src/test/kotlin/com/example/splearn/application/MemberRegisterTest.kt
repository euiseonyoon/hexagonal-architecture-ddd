package com.example.splearn.application

import com.example.splearn.application.provided.MemberRegister
import com.example.splearn.application.required.SplearnTestConfig
import com.example.splearn.domain.DuplicateEmailException
import com.example.splearn.domain.MemberFixture
import com.example.splearn.domain.MemberStatus
import jakarta.transaction.Transactional
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
}

