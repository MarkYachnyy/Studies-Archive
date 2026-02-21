package ru.iachnyi.dsr.practice.security;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.iachnyi.dsr.practice.entity.User;
import ru.iachnyi.dsr.practice.service.UserService;

@Component
public class SecurityUtils {

    @Autowired
    private final UserService service = new UserService();

    public Long getCurrentUserId() {
        String name = getCurrentUserName();
        return name == null ? null : ((User) service.loadUserByUsername(name)).getId();
    }

    public String getCurrentUserName(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }
}
