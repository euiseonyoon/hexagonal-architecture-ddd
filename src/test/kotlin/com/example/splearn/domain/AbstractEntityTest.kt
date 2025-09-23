package com.example.splearn.domain

import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.application.member.required.MemberRepository
import com.example.splearn.application.member.required.SplearnTestConfig
import com.example.splearn.domain.member.MemberFixture.Companion.createMemberRegisterRequest
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@Import(SplearnTestConfig::class)
class AbstractEntityTest(
    private val memberRegister: MemberRegister,
    private val em: EntityManager,
    private val memberRepository: MemberRepository,
) {
    @Test
    fun `test equals`() {
        // GIVEN
        val member = memberRegister.register(createMemberRegisterRequest())
        val memberDetailRealObject = member.detail.also {
            assertTrue { it !is HibernateProxy }
        }
        em.clear()

        // WHEN
        val foundMember = memberRepository.findById(member.id!!).also { assertNotNull(it) }
        val foundMemberDetailProxy = foundMember!!.detail

        // THEN
        assertTrue { foundMember == member }
        assertTrue { foundMemberDetailProxy is HibernateProxy }
        assertTrue { memberDetailRealObject == foundMember.detail }
    }

    @Test
    fun `test hashCode`() {
        // GIVEN
        val member = memberRegister.register(createMemberRegisterRequest()).also {
            assertTrue { it !is HibernateProxy }
        }
        em.clear()

        // WHEN
        val foundMember = memberRepository.findById(member.id!!)
        val foundMemberDetailProxy = foundMember!!.detail

        // THEN : equals가 true면, hashCode도 동일해야함
        // real 객체끼리 동일성 체크
        assertTrue { foundMember !is HibernateProxy }
        assertTrue { foundMember == member }
        assertTrue { foundMember.hashCode() == member.hashCode() }

        // proxy 객체 & real 객체 동일성 체크
        // MemberDetail real 객체 확인
        assertTrue { member.detail !is HibernateProxy }
        // MemberDetail initialize 안된 proxy 객체 확인
        assertTrue { foundMemberDetailProxy is HibernateProxy }
        assertFalse { Hibernate.isInitialized(foundMemberDetailProxy) }

        assertTrue { foundMemberDetailProxy == member.detail }
        assertTrue { foundMemberDetailProxy.hashCode() == member.detail.hashCode() }
    }
}
