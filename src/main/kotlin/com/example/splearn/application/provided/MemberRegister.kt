package com.example.splearn.application.provided

import com.example.splearn.domain.Member
import com.example.splearn.domain.MemberRegisterRequest
import jakarta.validation.Valid

/**
 * 회원의 등록과 관련된 기능을 제공한다.
 * */
interface MemberRegister {
    fun register(@Valid registerRequest: MemberRegisterRequest): Member

    fun activate(memberId: Long): Member
}
