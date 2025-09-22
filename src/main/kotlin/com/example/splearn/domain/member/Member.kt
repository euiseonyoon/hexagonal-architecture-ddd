package com.example.splearn.domain.member

import com.example.splearn.domain.AbstractEntity
import com.example.splearn.domain.shared.Email
import jakarta.persistence.*
import org.hibernate.annotations.NaturalId

@Entity
class Member protected constructor() : AbstractEntity() {
    @NaturalId
    lateinit var email: Email
        protected set

    lateinit var nickname: String
        protected set

    lateinit var passwordHash: String
        protected set

    lateinit var status: MemberStatus
        protected set

    lateinit var detail: MemberDetail
        protected set

    fun isActive(): Boolean = this.status == MemberStatus.ACTIVE

    fun activate() {
        require(this.status == MemberStatus.PENDING) {
            "Pending 상태가 아닙니다."
        }
        this.status = MemberStatus.ACTIVE
        this.detail.activate()
    }

    fun deactivate() {
        require(this.status == MemberStatus.ACTIVE) {
            "Active 상태가 아닙니다."
        }
        this.status = MemberStatus.DEACTIVATED
        detail.deactivate()
    }

    fun verifyPassword(password: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(password, passwordHash)
    }

    fun updateInfo(updateRequest: MemberInfoUpdateRequest) {
        require(this.status == MemberStatus.ACTIVE) {
            "Active 상태가 아닙니다."
        }
        updateRequest.nickname?.let { this.nickname = it }
        this.detail.setProfileInfo(updateRequest.profileAddress, updateRequest.introduction)
    }

    fun changePassword(password: String, passwordEncoder: PasswordEncoder) {
        this.passwordHash = passwordEncoder.encode(password)
    }

    companion object {
        @JvmStatic
        fun register(registerRequest: MemberRegisterRequest, passwordEncoder: PasswordEncoder): Member {
            val member = Member()
            member.email = Email.create(registerRequest.email)
            member.nickname = registerRequest.nickname
            member.passwordHash = passwordEncoder.encode(registerRequest.password)
            member.status = MemberStatus.PENDING

            member.detail = MemberDetail.create()

            return member
        }
    }
}
