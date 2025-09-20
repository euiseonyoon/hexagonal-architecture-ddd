package com.example.splearn.domain

data class MemberRegisterRequest(
    val email: Email,
    val nickname: String,
    val password: String,
)