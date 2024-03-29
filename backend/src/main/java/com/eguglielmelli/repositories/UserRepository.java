package com.eguglielmelli.repositories;
import com.eguglielmelli.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByEmail(String email);
}
