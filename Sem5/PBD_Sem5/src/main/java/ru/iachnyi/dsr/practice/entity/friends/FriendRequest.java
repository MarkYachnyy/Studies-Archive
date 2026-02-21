package ru.iachnyi.dsr.practice.entity.friends;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Table(name="friends")
@Data
public class FriendRequest {
    @EmbeddedId
    private FriendRequestId people;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;
    private Date date;
}
