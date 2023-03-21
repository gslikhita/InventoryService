package com.olx.inventoryManagementSystem.repository;

import com.olx.inventoryManagementSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JPAUserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
