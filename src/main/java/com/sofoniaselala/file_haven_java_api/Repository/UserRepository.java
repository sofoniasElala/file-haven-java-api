package com.sofoniaselala.file_haven_java_api.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sofoniaselala.file_haven_java_api.Model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsernameIgnoreCase(String username);
}