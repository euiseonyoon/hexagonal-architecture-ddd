package com.example.splearn.application.required

import com.example.splearn.domain.Member
import org.springframework.data.repository.Repository

/**
 * 회원정보를 저장하거나 조회한다.
 * */
interface MemberRepository : Repository<Member, Long> {
    fun save(member: Member): Member
}
