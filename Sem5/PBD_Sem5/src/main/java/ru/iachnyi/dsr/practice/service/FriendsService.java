package ru.iachnyi.dsr.practice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iachnyi.dsr.practice.entity.User;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequest;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequestId;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequestStatus;
import ru.iachnyi.dsr.practice.repository.FriendsRepository;
import ru.iachnyi.dsr.practice.repository.UserRepository;
import ru.iachnyi.dsr.practice.response_classes.NameAndDateFriendRelation;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private UserRepository userRepository;

    public List<NameAndDateFriendRelation> findAllRequestsSentByUser(Long userId) {
        return friendsRepository.findAllByPeople_senderIdAndStatus(userId, FriendRequestStatus.SENT)
                .stream().map(req -> new NameAndDateFriendRelation(userRepository.findById(req.getPeople().getReceiverId()).get().getName(), req.getDate().toString()))
                .collect(Collectors.toList());
    }

    public List<NameAndDateFriendRelation> findAllRequestsReceivedByUser(Long userId) {
        return friendsRepository.findAllByPeople_receiverIdAndStatus(userId, FriendRequestStatus.SENT)
                .stream().map(req -> new NameAndDateFriendRelation(userRepository.findById(req.getPeople().getSenderId()).get().getName(), req.getDate().toString()))
                .collect(Collectors.toList());
    }

    public List<NameAndDateFriendRelation> findAllFriends(Long userId) {
        Set<NameAndDateFriendRelation> received = friendsRepository.findAllByPeople_receiverIdAndStatus(userId, FriendRequestStatus.ACCEPTED)
                .stream().map(req -> new NameAndDateFriendRelation(userRepository.findById(req.getPeople().getSenderId()).
                        orElse(new User()).getName(), req.getDate().toString()))
                .collect(Collectors.toSet());
        Set<NameAndDateFriendRelation> sent = friendsRepository.findAllByPeople_senderIdAndStatus(userId, FriendRequestStatus.ACCEPTED)
                .stream().map(req -> new NameAndDateFriendRelation(userRepository.findById(req.getPeople().getReceiverId()).
                        orElse(new User()).getName(), req.getDate().toString()))
                .collect(Collectors.toSet());
        received.addAll(sent);
        return new ArrayList<>(received);
    }

    public void sendFriendRequest(Long from , Long to) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setPeople(new FriendRequestId(from, to));
        friendRequest.setStatus(FriendRequestStatus.SENT);
        friendRequest.setDate(Date.valueOf(LocalDate.now()));
        friendsRepository.save(friendRequest);
    }

    public List<FriendRequest> findAllRequestsBySenderIdAndReceiverId(Long senderId, Long receiverId) {
        return friendsRepository.findAllByPeople_SenderIdAndPeople_ReceiverId(senderId, receiverId);
    }

    public void addFriend(Long firstId, Long secondId) {
        List<FriendRequest> list = findAllRequestsBySenderIdAndReceiverId(firstId, secondId);
        list.addAll(findAllRequestsBySenderIdAndReceiverId(secondId, firstId));
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequest.setDate(Date.valueOf(LocalDate.now()));
        if(!list.isEmpty()) {
            friendRequest.setPeople(new FriendRequestId(list.getFirst().getPeople().getSenderId(), list.getFirst().getPeople().getReceiverId()));
        } else {
            friendRequest.setPeople(new FriendRequestId(firstId, secondId));
        }
        friendsRepository.save(friendRequest);
    }
}
