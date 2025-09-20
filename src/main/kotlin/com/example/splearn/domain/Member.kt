package com.example.splearn.domain

class Member private constructor() {

    lateinit var email: Email
        get
        private set

    lateinit var nickName: String
        get
        private set

    lateinit var passwordHash: String
        get
        private set

    lateinit var status: MemberStatus
        get
        private set

    fun isActive(): Boolean = this.status == MemberStatus.ACTIVE

    fun activate() {
        require(this.status == MemberStatus.PENDING) {
            "Pending 상태가 아닙니다."
        }
        this.status = MemberStatus.ACTIVE
    }

    fun deactivate() {
        require(this.status == MemberStatus.ACTIVE) {
            "Active 상태가 아닙니다."
        }
        this.status = MemberStatus.DEACTIVATED
    }

    fun verifyPassword(password: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(password, passwordHash)
    }

    fun changeNickname(nickname: String) {
        this.nickName = nickname
    }

    fun changePassword(password: String, passwordEncoder: PasswordEncoder) {
        this.passwordHash = passwordEncoder.encode(password)
    }

    companion object {
        @JvmStatic
        fun register(registerRequest: MemberRegisterRequest, passwordEncoder: PasswordEncoder): Member {
            val member = Member()
            member.email = registerRequest.email
            member.nickName = registerRequest.nickname
            member.passwordHash = passwordEncoder.encode(registerRequest.password)
            member.status = MemberStatus.PENDING

            return member
        }
    }
}
