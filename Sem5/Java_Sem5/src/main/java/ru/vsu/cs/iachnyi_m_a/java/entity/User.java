package ru.vsu.cs.iachnyi_m_a.java.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    private String email;
    private String password;

    public User(User user1){
        this.id = user1.id;
        this.name = user1.name;
        this.email = user1.email;
        this.password = user1.password;
    }
}
