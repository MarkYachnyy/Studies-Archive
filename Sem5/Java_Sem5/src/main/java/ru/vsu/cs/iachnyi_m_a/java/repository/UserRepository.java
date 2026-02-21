package ru.vsu.cs.iachnyi_m_a.java.repository;

import ru.vsu.cs.iachnyi_m_a.java.entity.User;

import java.util.Optional;

public interface UserRepository extends DataRepository<User, Long>{
    public Optional<User> findByEmail(String email);
}
