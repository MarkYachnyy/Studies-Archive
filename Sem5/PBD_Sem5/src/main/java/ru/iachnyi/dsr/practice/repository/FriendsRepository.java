package ru.iachnyi.dsr.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequest;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequestId;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequestStatus;

import java.util.List;

public interface FriendsRepository extends JpaRepository<FriendRequest, FriendRequestId> {

    List<FriendRequest> findAllByPeople_receiverIdAndStatus(Long people_receiverId, FriendRequestStatus status);

    List<FriendRequest> findAllByPeople_senderIdAndStatus(Long people_senderId, FriendRequestStatus status);

    List<FriendRequest> findAllByPeople_SenderIdAndPeople_ReceiverId(long senderId, long receiverId);

}
