package ru.vsu.cs.iachnyi_m_a.java.servlets.response_entity;

import lombok.Data;

@Data
public class SimpleSuccessOrErrorResponse {
    private String success;
    private String error;
}
