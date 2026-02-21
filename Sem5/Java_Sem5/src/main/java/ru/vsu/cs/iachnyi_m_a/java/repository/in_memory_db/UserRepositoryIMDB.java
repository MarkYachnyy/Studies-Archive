package ru.vsu.cs.iachnyi_m_a.java.repository.in_memory_db;

import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserRepositoryIMDB implements UserRepository {

    private InMemoryDatabase database;

    public UserRepositoryIMDB(InMemoryDatabase database){
        this.database = database;
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> res = database.getAllUsers().stream().filter(user -> user.getId() == id).findFirst();
        return res;
    }

    @Override
    public List<User> findAll() {
        List<User> res = database.getAllUsers();
        return res;
    }

    @Override
    public User save(User entity) {
        List<User> allUsers = database.getAllUsers();
        if (allUsers.stream().anyMatch(user -> user.getId() == entity.getId())) {
            return database.updateUser(entity);
        } else {
            return database.insertUser(entity);
        }
    }

    @Override
    public void delete(Long id) {}

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> res = database.getAllUsers().stream().filter(user -> user.getEmail().equals(email)).findFirst();
        return res;
    }
}
