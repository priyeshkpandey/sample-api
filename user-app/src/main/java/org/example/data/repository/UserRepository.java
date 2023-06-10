package org.example.data.repository;

import org.example.data.model.User;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByEmailAndPassword(String email, String password);
    Iterable<User> findAllByEmailAndPassword(String email, String password);
    Optional<User> findByToken(String token);
}
