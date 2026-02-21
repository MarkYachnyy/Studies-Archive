package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;
import ru.vsu.cs.iachnyi_m_a.java.servlets.ServletUtils;
import ru.vsu.cs.iachnyi_m_a.java.servlets.response_entity.SimpleSuccessOrErrorResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = ServletUtils.parseJson(req, User.class);
        resp.setContentType("application/json");
        if(userService.checkCredentialsNotEncoded(user.getEmail(), user.getPassword())){
            User res = new User(0, null, user.getEmail(), userService.findUserByEmail(user.getEmail()).getPassword());
            resp.getWriter().println(gson.toJson(res));
            resp.getWriter().flush();
        } else {
            SimpleSuccessOrErrorResponse res = new SimpleSuccessOrErrorResponse();
            res.setError("Неверный логин или пароль");
            resp.getWriter().println(gson.toJson(res));
            resp.getWriter().flush();
        }

    }
}
