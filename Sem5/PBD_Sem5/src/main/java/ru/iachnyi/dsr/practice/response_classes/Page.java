package ru.iachnyi.dsr.practice.response_classes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Page <T> {
    private int count;
    private List<T> content;

}
