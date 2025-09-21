package com.example.splearn.domain

import jakarta.persistence.*
import org.hibernate.annotations.NaturalId

@Entity
class Member protected constructor() {
    @Id
    @GeneratedValue
    val id: Long = 0L

    @Embedded
    @NaturalId
    lateinit var email: Email
        internal set

    // change to internal : JPA requires a way to set the value of this property
    lateinit var nickName: String
        internal set

    lateinit var passwordHash: String
        internal set

    @Enumerated(value = EnumType.STRING)
    lateinit var status: MemberStatus
        internal set

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
            member.email = Email(registerRequest.email)
            member.nickName = registerRequest.nickname
            member.passwordHash = passwordEncoder.encode(registerRequest.password)
            member.status = MemberStatus.PENDING

            return member
        }
    }
}
