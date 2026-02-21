package ru.vsu.cs.iachnyi_m_a.java.servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ServletUtils {

    private static final Gson gson = new Gson();

    public static <T> T parseJson(HttpServletRequest request, Class<T> clazz) throws IOException {
        try (Reader reader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        }
    }

    public static boolean checkCredentialsEncoded(HttpServletRequest request, UserService userService) {
        String email = request.getHeader("email");
        String password_hash = request.getHeader("password_hash");
        if(email == null || password_hash == null) {
            return false;
        } else {
            User user = userService.findUserByEmail(email);
            return user != null && user.getPassword().equals(password_hash);
        }
    }
}
