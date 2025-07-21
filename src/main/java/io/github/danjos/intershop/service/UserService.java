package io.github.danjos.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import io.github.danjos.intershop.repository.UserRepository;
import io.github.danjos.intershop.model.User;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getCurrentUser() {

        return userRepository.findByUsername("currentUser").orElseThrow(); // Todo in the future with spring security
    }
}
