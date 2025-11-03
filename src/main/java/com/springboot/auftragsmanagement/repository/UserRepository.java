package com.springboot.auftragsmanagement.repository;

import com.springboot.auftragsmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
