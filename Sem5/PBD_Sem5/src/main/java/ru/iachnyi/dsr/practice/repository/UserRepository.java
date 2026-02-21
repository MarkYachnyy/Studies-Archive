package ru.iachnyi.dsr.practice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.iachnyi.dsr.practice.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByName(String username);
}
