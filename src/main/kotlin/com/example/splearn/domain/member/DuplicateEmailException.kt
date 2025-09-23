package com.example.splearn.domain.member

// @ResponseStatus(HttpStatus.CONFLICT). <- 이렇게 해도 되지만, 그러면 adapter의 코드가 domain 내부로 침투하게 된다.
class DuplicateEmailException(message: String): RuntimeException(message) {
}