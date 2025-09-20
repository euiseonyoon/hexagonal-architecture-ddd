package com.example.splearn.application.provided

import com.example.splearn.domain.Member
import com.example.splearn.domain.MemberRegisterRequest

/**
 * 회원의 등록과 관련된 기능을 제공한다.
 * */
interface MemberResister {
    fun register(registerRequest: MemberRegisterRequest): Member
}
