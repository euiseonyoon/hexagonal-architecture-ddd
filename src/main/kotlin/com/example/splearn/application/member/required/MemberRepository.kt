package com.example.splearn.application.member.required

import com.example.splearn.domain.shared.Email
import com.example.splearn.domain.member.Member
import com.example.splearn.domain.member.Profile
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

/**
 * 회원정보를 저장하거나 조회한다.
 * */
interface MemberRepository : Repository<Member, Long> {
    fun save(member: Member): Member

    fun findByEmail(email: Email): Member?

    fun findById(id: Long): Member?

    @Query("SELECT m FROM Member m WHERE m.detail.profile = :profile")
    fun findByProfile(profile: Profile): Member?
}
