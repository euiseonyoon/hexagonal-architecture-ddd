package com.example.splearn.application.member.required

import com.example.splearn.domain.member.Member
import com.example.splearn.domain.member.MemberFixture.Companion.createMemberRegisterRequest
import com.example.splearn.domain.member.MemberFixture.Companion.createPasswordEncoder
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertNotEquals

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var em: EntityManager

    @Test
    fun createMember() {
        // GIVEN
        val member = Member.register(
            createMemberRegisterRequest(),
            createPasswordEncoder()
        )

        // WHEN
        memberRepository.save(member)
        em.flush()

        // THEN
        assertNotEquals(0L, member.id)
    }

    @Test
    fun duplicateEmailFail() {
        // GIVEN
        val member = Member.register(
            createMemberRegisterRequest(),
            createPasswordEncoder()
        )
        memberRepository.save(member)
        em.flush()
        em.clear()

        // WHEN
        val member2 = Member.register(
            createMemberRegisterRequest(),
            createPasswordEncoder()
        )

        // THEN
        assertThrows<Exception> {
            memberRepository.save(member2)
            em.flush()
        }
    }
}
