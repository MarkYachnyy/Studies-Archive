package ru.vsu.cs.iachnyi_m_a.java.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Seller {
    private long id;
    private String name;

    public Seller(Seller seller1) {
        this.id = seller1.id;
        this.name = seller1.name;
    }
}
