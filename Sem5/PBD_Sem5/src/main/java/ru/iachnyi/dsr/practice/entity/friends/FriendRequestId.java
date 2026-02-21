package ru.iachnyi.dsr.practice.entity.friends;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestId implements Serializable {
    private Long senderId;
    private Long receiverId;
}