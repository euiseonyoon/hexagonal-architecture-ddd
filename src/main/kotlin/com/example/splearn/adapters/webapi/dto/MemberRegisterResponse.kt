package com.example.splearn.adapters.webapi.dto

import com.example.splearn.domain.member.Member
import kotlinx.serialization.Serializable

@Serializable
data class MemberRegisterResponse(
    val memberId: Long,
    val email: String,
) {
    companion object {
        fun of(member: Member): MemberRegisterResponse = MemberRegisterResponse(member.id!!, member.email.address)
    }
}
