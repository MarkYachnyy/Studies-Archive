package ru.iachnyi.dsr.practice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.iachnyi.dsr.practice.entity.User;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequest;
import ru.iachnyi.dsr.practice.entity.friends.FriendRequestStatus;
import ru.iachnyi.dsr.practice.repository.UserRepository;
import ru.iachnyi.dsr.practice.response_classes.NameAndDateFriendRelation;
import ru.iachnyi.dsr.practice.response_classes.Page;
import ru.iachnyi.dsr.practice.response_classes.SimpleSuccessOrErrorResponse;
import ru.iachnyi.dsr.practice.security.SecurityUtils;
import ru.iachnyi.dsr.practice.service.FriendsService;

import java.util.List;

@RestController
public class FriendsController {
    private static final Logger log = LoggerFactory.getLogger(FriendsController.class);
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/friends/all")
    public List<NameAndDateFriendRelation> getAllFriends() {
       return friendsService.findAllFriends(securityUtils.getCurrentUserId());
    }

    @GetMapping("/api/friends/part/{from}-{to}")
    public Page<NameAndDateFriendRelation> getSomeFriends(@PathVariable int from, @PathVariable int to) {
        List<NameAndDateFriendRelation> all = friendsService.findAllFriends(securityUtils.getCurrentUserId());
        return new Page<>(all.size(), all.subList(from, Math.min(all.size(), to)));
    }

    @GetMapping("/api/friends/all-requests-received")
    public List<NameAndDateFriendRelation> getRequestsReceived() {
        List<NameAndDateFriendRelation> res = friendsService.findAllRequestsReceivedByUser(securityUtils.getCurrentUserId());
        return res;
    }

    @GetMapping("/api/friends/all-requests-sent")
    public List<NameAndDateFriendRelation> getRequestsSent() {
        return friendsService.findAllRequestsSentByUser(securityUtils.getCurrentUserId());
    }

    @PostMapping("/api/friends/send-request/{name}")
    public SimpleSuccessOrErrorResponse sendRequest(@PathVariable String name) {
        SimpleSuccessOrErrorResponse res = new SimpleSuccessOrErrorResponse();
        User user = userRepository.findByName(name).orElse(null);
        if (user == null) {
            res.setError("Пользователя с таким ником не существует");
        } else if (securityUtils.getCurrentUserName().equals(name)) {
            res.setError("Это ваш ник!");
        } else {
            long currId = securityUtils.getCurrentUserId();
            long userId = user.getId();
            List<FriendRequest> requestsSent = friendsService.findAllRequestsBySenderIdAndReceiverId(currId, userId);
            if (!requestsSent.isEmpty()) {
                FriendRequest request = requestsSent.getFirst();
                res.setError(request.getStatus() == FriendRequestStatus.ACCEPTED ? "Данный пользватель уже у вас в друзьях" : "Вы уже отправили запрос данному пользователю");
            } else {
                List<FriendRequest> requestsReceived = friendsService.findAllRequestsBySenderIdAndReceiverId(userId, currId);
                if (!requestsReceived.isEmpty()) {
                    FriendRequest request = requestsReceived.getFirst();
                    if (request.getStatus() == FriendRequestStatus.SENT) {
                        friendsService.addFriend(userId, currId);
                        res.setSuccess("Пользователь добавлен в друзья");
                    } else {
                        res.setError("Данный пользватель уже у вас в друзьях");
                    }
                } else {
                    friendsService.sendFriendRequest(currId, userId);
                    res.setSuccess("Запрос успешно отправлен");
                }
            }

        }
        return res;
    }

    @GetMapping("/api/friends/are-friends")
    public boolean areFriends(@RequestParam String name1, @RequestParam String name2) {
        long id1 = userRepository.findByName(name1).orElse(new User()).getId();
        return friendsService.findAllFriends(id1).stream().anyMatch(f -> f.getName().equals(name2));
    }

    @PostMapping("/api/friends/add-friend/{name}")
    public void addFriend(@PathVariable String name) {
        Long firstId = securityUtils.getCurrentUserId();
        Long secondId = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("kys now")).getId();
        friendsService.addFriend(firstId, secondId);
    }
}
