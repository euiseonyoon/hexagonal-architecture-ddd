package com.example.splearn.adapters.webapi

import com.example.splearn.TestUtils.Companion.makePostCall
import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.domain.member.MemberFixture.Companion.createMember
import com.example.splearn.domain.member.MemberFixture.Companion.createMemberRegisterRequest
import com.example.splearn.domain.member.MemberRegisterRequest
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

@WebMvcTest(MemberApi::class)
class MemberApiWebMvcTest(
    private val mockMvcTester: MockMvcTester,
) {
    @MockitoBean
    lateinit var memberRegister: MemberRegister

    val json = Json

    @Test
    fun registerMember() {
        // GIVEN
        val member = createMember(1L)
        Mockito.`when`(memberRegister.register(any())).thenReturn(member)
        val requestJson = createMemberRegisterRequest().let {
            json.encodeToString(MemberRegisterRequest.serializer(), it)
        }

        // WHEN & THEN
        assertThat(makePostCall(mockMvcTester, "/api/members", requestJson))
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .extractingPath("$.memberId").asNumber().isEqualTo(1)
    }

    @Test
    fun `register invalid request`() {
        // GIVEN
        val requestJson =createMemberRegisterRequest("invalid email").let {
            json.encodeToString(MemberRegisterRequest.serializer(), it)
        }

        // WHEN & THEN
        assertThat(makePostCall(mockMvcTester, "/api/members", requestJson))
            .hasStatus(HttpStatus.BAD_REQUEST)
    }
}
