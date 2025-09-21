package com.example.splearn.application

import com.example.splearn.application.provided.MemberRegister
import com.example.splearn.application.required.EmailSender
import com.example.splearn.application.required.MemberRepository
import com.example.splearn.domain.*
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
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailSender: EmailSender,
) : MemberRegister {

    @Transactional
    override fun register(@Valid registerRequest: MemberRegisterRequest): Member {
        checkDuplicateEmail(Email(registerRequest.email))

        val member = Member.register(registerRequest, passwordEncoder)

        memberRepository.save(member)

        emailSender.send(member.email, "가입 신청완료", "가입이 신청이 완료되었습니다.")

        return member
    }

    private fun checkDuplicateEmail(email: Email) {
        if (memberRepository.findByEmail(email) != null) {
            throw DuplicateEmailException("이미 사용중인 이메일입니다. email={$email.address}")
        }
    }
}