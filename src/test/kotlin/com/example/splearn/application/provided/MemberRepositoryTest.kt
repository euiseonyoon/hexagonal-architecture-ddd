package com.example.splearn.application.provided

import com.example.splearn.application.member.required.MemberRepository
import com.example.splearn.domain.MemberFixture.Companion.createMemberRegisterRequest
import com.example.splearn.domain.MemberFixture.Companion.createPasswordEncoder
import com.example.splearn.domain.member.Member
import com.example.splearn.domain.member.MemberStatus
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertEquals


@DataJpaTest
class MemberRepositoryTest(
    val memberRepository: MemberRepository,
    val entityManager: EntityManager,
) {
    @Test
    fun createMember() {
        // GIVEN
        val member = Member.register(createMemberRegisterRequest(), createPasswordEncoder())
        assertNull(member.id)

        // WHEN
        memberRepository.save(member)
        assertNotNull(member.id)

        entityManager.flush()
        entityManager.clear()

        // THEN
        val found = memberRepository.findById(member.id!!)
        assertNotNull(found)
        assertEquals(MemberStatus.PENDING, found.status)
        assertNotNull(found.detail.registeredAt)
    }
}
