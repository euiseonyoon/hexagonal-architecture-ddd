package com.example.splearn.application.provided

import com.example.splearn.domain.Member

interface MemberFinder {
    fun find(memberId: Long): Member
}
