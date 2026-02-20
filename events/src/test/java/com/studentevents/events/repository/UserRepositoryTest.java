package com.studentevents.events.repository;

import com.studentevents.events.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_shouldReturnTrueIfUserExists() {
        userRepository.save(new User("a@mail.com", "1234", "Alice"));

        assertTrue(userRepository.existsByEmail("a@mail.com"));
        assertFalse(userRepository.existsByEmail("n@mail.com"));
    }

    @Test
    void findByEmail_shouldReturnUserIfExists() {
        userRepository.save(new User("b@mail.com", "pass", "Bob"));

        var userOpt = userRepository.findByEmail("b@mail.com");

        assertTrue(userOpt.isPresent());
        assertEquals("Bob", userOpt.get().getName());
    }
}
