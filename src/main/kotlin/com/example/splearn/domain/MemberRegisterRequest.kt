package com.example.splearn.domain

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class MemberRegisterRequest(
    @field:Email
    val email: String,
    @field:Size(min = 3, max = 20)
    val nickname: String,
    @field:Size(min = 5, max = 20)
    val password: String,
)
