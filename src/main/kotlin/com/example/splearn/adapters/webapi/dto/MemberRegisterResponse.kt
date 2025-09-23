package com.example.splearn.adapters.webapi.dto

import com.example.splearn.domain.member.Member

data class MemberRegisterResponse(
    val memberId: Long,
    val emailAddress: String,
) {
    companion object {
        fun of(member: Member): MemberRegisterResponse = MemberRegisterResponse(member.id!!, member.email.address)
    }
}
