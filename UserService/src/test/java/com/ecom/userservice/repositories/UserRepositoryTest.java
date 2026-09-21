package com.ecom.userservice.repositories;

import com.ecom.userservice.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        User user = new User("1", "testuser", "test@example.com",new ArrayList<>());
        userRepository.save(user);

        Optional<User> found = userRepository.findById("1");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testFindByUsername() {
        User user = new User("2", "anotheruser", "another@example.com",new ArrayList<>());
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("anotheruser");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("another@example.com");
    }

    @Test
    void testExistsByUsernameAndEmail() {
        User user = new User("3", "checkuser", "check@example.com",new ArrayList<>());
        userRepository.save(user);

        assertThat(userRepository.existsByUsername("checkuser")).isTrue();
        assertThat(userRepository.existsByEmail("check@example.com")).isTrue();
    }
}
