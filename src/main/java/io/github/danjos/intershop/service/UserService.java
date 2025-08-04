package io.github.danjos.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import io.github.danjos.intershop.repository.UserRepository;
import io.github.danjos.intershop.model.User;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Mono<User> getCurrentUser() {
        return userRepository.findByUsername("currentUser")
                .switchIfEmpty(Mono.error(new RuntimeException("Current user not found")));
    }

    public User getCurrentUserBlocking() {
        return getCurrentUser().block();
    }
}
