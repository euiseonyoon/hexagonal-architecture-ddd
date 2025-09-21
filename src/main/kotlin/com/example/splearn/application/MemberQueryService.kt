package com.example.splearn.application

import com.example.splearn.application.provided.MemberFinder
import com.example.splearn.application.required.MemberRepository
import com.example.splearn.domain.Member
import com.example.splearn.domain.MemberNotFoundException
import org.springframework.stereotype.Service

@Service
class MemberQueryService(
    private val memberRepository: MemberRepository
): MemberFinder {
    override fun find(memberId: Long): Member {
        return memberRepository.findById(memberId) ?: throw MemberNotFoundException("멤버를 찾을수 없습니다. id={$memberId}")
    }
}