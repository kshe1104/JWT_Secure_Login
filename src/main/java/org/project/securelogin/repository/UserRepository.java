package org.project.securelogin.repository;

import org.project.securelogin.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> { //동명이인을 고려하여 email로 구분
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
