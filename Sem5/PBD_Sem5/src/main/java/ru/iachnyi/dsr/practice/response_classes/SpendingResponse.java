package ru.iachnyi.dsr.practice.response_classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpendingResponse {
    private long id;
    private String name;
    private String payerName;
    private String creatorName;
    private String Date;
    private Map<String, Integer> debts;
}
