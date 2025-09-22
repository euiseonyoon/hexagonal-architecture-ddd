package com.example.splearn.application

import com.example.splearn.application.member.provided.MemberFinder
import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.application.required.SplearnTestConfig
import com.example.splearn.domain.*
import com.example.splearn.domain.member.Member
import com.example.splearn.domain.member.MemberNotFoundException
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
@Import(SplearnTestConfig::class)
class MemberFinderTest(
    private val memberRegister: MemberRegister,
    private val memberFinder: MemberFinder,
    private val entityManager: EntityManager,
) {
    lateinit var member: Member

    @BeforeEach
    fun setup() {
        member = memberRegister.register(MemberFixture.createMemberResiterRequest())
        entityManager.flush()
    }

    @Test
    fun find() {
        entityManager.clear()

        // WHEN
        val found = memberFinder.find(member.id)

        // THEN
        assertEquals(member.id, found.id)
    }

    @Test
    fun findFail() {
        // WHEN & THEN
        assertThrows<MemberNotFoundException> {
            memberFinder.find(999L)
        }
    }
}

