package com.example.splearn.adapters.webapi

import com.example.splearn.TestUtils.Companion.makePostCall
import com.example.splearn.adapters.webapi.dto.MemberRegisterResponse
import com.example.splearn.application.member.provided.MemberRegister
import com.example.splearn.application.member.required.MemberRepository
import com.example.splearn.domain.member.MemberFixture.Companion.createMemberRegisterRequest
import com.example.splearn.domain.member.MemberRegisterRequest
import com.example.splearn.domain.member.MemberStatus
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberApiTest(
    val mockMvcTester: MockMvcTester,
    val memberRepository: MemberRepository,
    val memberRegister: MemberRegister,
    val em: EntityManager,
) {
    val json = Json

    @Test
    fun register() {
        // GIVEN
        val request = createMemberRegisterRequest()
        val requestJson = json.encodeToString(MemberRegisterRequest.serializer(), request)

        // WHEN
        val result = makePostCall(mockMvcTester, "/api/members", requestJson)

        // THEN
        assertThat(result)
            .hasStatus(HttpStatus.OK)
//            .bodyJson()
//            .hasPathSatisfying("$.memberId") { assertThat(it).isNotNull }
//            .hasPathSatisfying("$.email") { assertThat(it).isEqualTo(request.email) }

        val response = json.decodeFromString(MemberRegisterResponse.serializer(), result.response.contentAsString)
        assertNotNull(response.memberId)
        assertEquals(request.email, response.email)

        val foundMember = memberRepository.findById(response.memberId)
        assertNotNull(foundMember)
        assertEquals(request.email, foundMember.email.address)
        assertEquals(MemberStatus.PENDING, foundMember.status)
    }

    @Test
    fun `register duplicate email`() {
        val request = createMemberRegisterRequest()
        memberRegister.register(request)
        em.flush()

        val requestJson = json.encodeToString(MemberRegisterRequest.serializer(), request)

        // THEN
        assertThat(makePostCall(mockMvcTester, "/api/members", requestJson))
            .apply(MockMvcResultHandlers.print()) // response 상세 print
            .hasStatus(HttpStatus.CONFLICT)

        // response body
        /**
         * {
         *    "type":"about:blank",
         *    "title":"Conflict",
         *    "status":409,
         *    "detail":"이미 사용중인 이메일입니다. email={test@gmail.com}",
         *    "instance":"/api/members",
         *    "timestamp":"2025-09-23T16:05:25.1943",
         *    "exception":"DuplicateEmailException"
         * }
         * */
    }
}
