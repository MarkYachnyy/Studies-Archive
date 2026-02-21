package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContext;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;
import ru.vsu.cs.iachnyi_m_a.java.servlets.ServletUtils;
import ru.vsu.cs.iachnyi_m_a.java.servlets.response_entity.SimpleSuccessOrErrorResponse;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(urlPatterns = "/api/register-user")
public class RegisterUserServlet extends HttpServlet {

    private UserService userService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try{
            User user = ServletUtils.parseJson(req, User.class);
            SimpleSuccessOrErrorResponse response = new SimpleSuccessOrErrorResponse();
            System.out.println(userService);
            if(userService.registerUser(user)){
                response.setSuccess("Пользователь успешно зарегистрирован");
            } else {
                response.setError("Пользователь не зарегистрирован");
            }
            resp.setContentType("text/json");
            resp.getWriter().println(gson.toJson(response));
            resp.getWriter().flush();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
