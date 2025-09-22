package com.example.splearn.application.member

import com.example.splearn.application.member.provided.MemberFinder
import com.example.splearn.application.member.required.MemberRepository
import com.example.splearn.domain.member.Member
import com.example.splearn.domain.member.MemberNotFoundException
import org.springframework.stereotype.Service

@Service
class MemberQueryService(
    private val memberRepository: MemberRepository
): MemberFinder {
    override fun find(memberId: Long): Member {
        return memberRepository.findById(memberId) ?: throw MemberNotFoundException("멤버를 찾을수 없습니다. id={$memberId}")
    }
}