package com.example.splearn.application

import com.example.splearn.application.provided.MemberResister
import com.example.splearn.application.required.EmailSender
import com.example.splearn.application.required.MemberRepository
import com.example.splearn.domain.Member
import com.example.splearn.domain.MemberRegisterRequest
import com.example.splearn.domain.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * 어느 driving adapter 에서 해당 serivce(MemberResister - input ports)를 주입받아서 사용한다.
 * emailSender interface(output ports)를 사용시 어떤 adapter가 사용될지는 아직 모른다.
 * */
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailSender: EmailSender,
) : MemberResister {

    override fun register(registerRequest: MemberRegisterRequest): Member {
        // validation

        val member = Member.register(registerRequest, passwordEncoder)

        memberRepository.save(member)

        emailSender.send(member.email, "가입 신청완료", "가입이 신청이 완료되었습니다.")

        return member
    }
}