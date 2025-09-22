package com.example.splearn.domain.member

import com.example.splearn.domain.AbstractEntity
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class MemberDetail protected constructor(): AbstractEntity() {
    var profile: Profile? = null
        protected set

    var introduction: String? = null
        protected set

    var registeredAt: LocalDateTime = LocalDateTime.now()

    var activatedAt: LocalDateTime? = null
        protected set

    var deactivatedAt: LocalDateTime? = null
        protected set

    fun activate() {
        require(this.activatedAt == null) { "이미 activatedAt은 설정 완료되었습니다." }
        this.activatedAt = LocalDateTime.now()
    }

    fun deactivate() {
        requireNotNull(this.activatedAt) { "activate가 되지 않았습니다." }
        require(this.deactivatedAt == null) { "이미 deactivatedAt은 설정 완료되었습니다." }
        this.deactivatedAt = LocalDateTime.now()
    }

    fun setProfileInfo(profile: String?, introduction: String?) {
        profile?.let { this.profile = Profile.create(profile) }
        introduction?.let { this.introduction = introduction }
    }

    companion object {
        @JvmStatic
        fun create(): MemberDetail {
            return MemberDetail()
        }
    }
}
