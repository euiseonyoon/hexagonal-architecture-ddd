package com.example.splearn.application.required

import com.example.splearn.application.member.required.MemberRepository
import com.example.splearn.domain.member.Member
import com.example.splearn.domain.MemberFixture.Companion.createMemberResiterRequest
import com.example.splearn.domain.MemberFixture.Companion.createPasswordEncoder
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
            createMemberResiterRequest(),
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
            createMemberResiterRequest(),
            createPasswordEncoder()
        )
        memberRepository.save(member)
        em.flush()
        em.clear()

        // WHEN
        val member2 = Member.register(
            createMemberResiterRequest(),
            createPasswordEncoder()
        )

        // THEN
        assertThrows<Exception> {
            memberRepository.save(member2)
            em.flush()
        }
    }
}
