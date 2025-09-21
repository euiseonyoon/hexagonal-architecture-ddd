package com.example.splearn.application

import com.example.splearn.application.provided.MemberResister
import com.example.splearn.application.required.EmailSender
import com.example.splearn.application.required.MemberRepository
import com.example.splearn.domain.Email
import com.example.splearn.domain.Member
import com.example.splearn.domain.MemberFixture
import com.example.splearn.domain.MemberStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class MemberRegisterTest {
    @Test
    fun registerTestStub() {
        val register: MemberResister = MemberService(
            memberRepository = MemberRepositoryStub(),
            passwordEncoder =  MemberFixture.createPasswordEncoder(),
            emailSender = EmailSenderStub()
        )

        val member = register.register(MemberFixture.createMemberResiterRequest())

        // THEN
        assertNotEquals(0L, member.id)
        assertEquals(MemberStatus.PENDING, member.status)
    }

    @Test
    fun registerTestMock() {
        val emailSenderMock: EmailSender = Mockito.mock(EmailSender::class.java)

        val register: MemberResister = MemberService(
            memberRepository = MemberRepositoryStub(),
            passwordEncoder =  MemberFixture.createPasswordEncoder(),
            emailSender = emailSenderMock
        )

        val member = register.register(MemberFixture.createMemberResiterRequest())

        // THEN
        assertNotEquals(0L, member.id)
        assertEquals(MemberStatus.PENDING, member.status)
        assertNotNull(member.email)

        verify(emailSenderMock, times(1)).send(eq(member.email), any(), any())
    }

    class MemberRepositoryStub : MemberRepository {
        override fun save(member: Member): Member {
            ReflectionTestUtils.setField(member, "id", 1L)
            return member
        }
    }

    class EmailSenderStub : EmailSender {
        override fun send(email: Email, title: String, content: String) {
        }
    }

    class EmailSenderMock : EmailSender {
        override fun send(email: Email, title: String, content: String) {
        }
    }
}