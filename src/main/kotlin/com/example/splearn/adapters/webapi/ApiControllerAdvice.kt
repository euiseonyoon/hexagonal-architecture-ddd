package com.example.splearn.adapters.webapi

import com.example.splearn.domain.member.DuplicateEmailException
import com.example.splearn.domain.member.DuplicateProfileAddressException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@RestControllerAdvice
class ApiControllerAdvice: ResponseEntityExceptionHandler() {
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ProblemDetail {
        return problemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception)
    }

    @ExceptionHandler(DuplicateEmailException::class, DuplicateProfileAddressException::class)
    fun handleDuplicateException(exception: DuplicateEmailException): ProblemDetail {
        return problemDetail(HttpStatus.CONFLICT, exception)
    }

    private fun problemDetail(httpStatus: HttpStatus, exception: Exception): ProblemDetail {
        // RFC9457
        val problemDetail = ProblemDetail.forStatusAndDetail(
            httpStatus,
            exception.message
        )

        problemDetail.setProperty("timestamp", LocalDateTime.now())
        problemDetail.setProperty("exception", exception.javaClass.simpleName)

        return problemDetail
    }
}
