package ru.iachnyi.dsr.practice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.iachnyi.dsr.practice.entity.debt.Debt;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "spendings")
@Data
@NoArgsConstructor
public class Spending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long creatorId;
    private Long payerId;
    private Date date;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "id.spendingId")
    Set<Debt> debts;

    public Spending(String name, Long creatorId, Long payerId, Date date, Set<Debt> debts) {
        this.name = name;
        this.creatorId = creatorId;
        this.payerId = payerId;
        this.date = date;
        this.debts = debts;
    }

    public void setDebts(Set<Debt> debts) {
        debts.forEach(d -> d.getId().setSpendingId(this.id));
        this.debts = debts;
    }
}
