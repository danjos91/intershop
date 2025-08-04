package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.User;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveSortingRepository<User, Long> {
    Mono<User> findByUsername(String username);
}
