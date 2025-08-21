package io.github.danjos.intershop.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import io.github.danjos.intershop.repository.UserRepository;
import io.github.danjos.intershop.model.User;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(userModel -> org.springframework.security.core.userdetails.User.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("USER")))
                .build());
    }
}
