package com.example.splearn.application.member.provided

import com.example.splearn.domain.member.Member

interface MemberFinder {
    fun find(memberId: Long): Member
}
