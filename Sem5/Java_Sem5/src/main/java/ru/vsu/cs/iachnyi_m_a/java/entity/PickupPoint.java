package ru.vsu.cs.iachnyi_m_a.java.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PickupPoint {
    private long id;
    private String address;
}
