package com.example.splearn.domain.member

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class ProfileTest {
    @Test
    fun profiles() {
        Profile.create("hello")
        Profile.create("hello1234")
        Profile.create("1234")
    }

    @Test
    fun badProfiles() {
        assertThrows<IllegalArgumentException> { Profile.create("") }
        assertThrows<IllegalArgumentException> { Profile.create("A") }
        assertThrows<IllegalArgumentException> { Profile.create("@") }
        assertThrows<IllegalArgumentException> { Profile.create("toooooooolongtoooooooolong") }
    }
}