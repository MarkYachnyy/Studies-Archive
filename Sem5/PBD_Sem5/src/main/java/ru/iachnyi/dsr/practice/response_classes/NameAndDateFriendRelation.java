package ru.iachnyi.dsr.practice.response_classes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NameAndDateFriendRelation {
    private String name;
    private String date;
}
