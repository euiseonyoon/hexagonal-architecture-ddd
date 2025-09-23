package com.example.splearn.adapters.webapi

import com.example.splearn.adapters.webapi.dto.MemberRegisterResponse
import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.domain.member.MemberRegisterRequest
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class MemberApi(
    private val memberRegister: MemberRegister
) {
    @PostMapping("/api/members")
    fun registerMember(
        @RequestBody @Valid request: MemberRegisterRequest
    ): MemberRegisterResponse {
        return memberRegister.register(request).let { MemberRegisterResponse.of(it) }
    }
}