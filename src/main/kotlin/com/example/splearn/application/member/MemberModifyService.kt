package com.example.splearn.application.member

import com.example.splearn.application.member.provided.MemberFinder
import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.application.member.required.EmailSender
import com.example.splearn.application.member.required.MemberRepository
import com.example.splearn.domain.member.*
import com.example.splearn.domain.shared.Email
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

/**
 * 어느 driving adapter 에서 해당 serivce(MemberResister - input ports)를 주입받아서 사용한다.
 * emailSender interface(output ports)를 사용시 어떤 adapter가 사용될지는 아직 모른다.
 * */
@Service
@Validated
class MemberModifyService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailSender: EmailSender,
    private val memberFinder: MemberFinder,
) : MemberRegister {

    @Transactional
    override fun register(@Valid registerRequest: MemberRegisterRequest): Member {
        checkDuplicateEmail(Email.create(registerRequest.email))

        val member = Member.register(registerRequest, passwordEncoder)

        memberRepository.save(member)

        emailSender.send(member.email, "가입 신청완료", "가입이 신청이 완료되었습니다.")

        return member
    }

    @Transactional
    override fun activate(memberId: Long): Member {
        val member = memberFinder.find(memberId)
        member.activate()
        return memberRepository.save(member)
    }

    @Transactional
    override fun deactivate(memberId: Long): Member {
        val member = memberFinder.find(memberId)
        member.deactivate()
        return memberRepository.save(member)
    }

    @Transactional
    override fun updateInfo(memberId: Long, memberInfoUpdateRequest: MemberInfoUpdateRequest,
    ): Member {
        val member = memberFinder.find(memberId)

        checkDuplicateProfile(member, memberInfoUpdateRequest.profileAddress)

        member.updateInfo(memberInfoUpdateRequest)
        return memberRepository.save(member)
    }

    private fun checkDuplicateEmail(email: Email) {
        if (memberRepository.findByEmail(email) != null) {
            throw DuplicateEmailException("이미 사용중인 이메일입니다. email={$email.address}")
        }
    }

    private fun checkDuplicateProfile(member: Member, profileAddress: String?) {
        if (profileAddress.isNullOrEmpty()) return
        if (member.detail.profile?.address.equals(profileAddress)) return

        if(memberRepository.findByProfile(Profile.create(profileAddress)) != null) {
            throw DuplicateProfileAddressException("이미 존재하는 프로필 주소입니다. profile_address : {$profileAddress}")
        }
    }
}