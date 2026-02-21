package ru.vsu.cs.iachnyi_m_a.java.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceResponse <T>{
    private boolean success;
    private String message;
    private T data;
}
