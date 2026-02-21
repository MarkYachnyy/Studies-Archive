package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;
import ru.vsu.cs.iachnyi_m_a.java.servlets.ServletUtils;

import java.io.IOException;

@WebServlet("/api/check-credentials")
public class AuthServlet extends HttpServlet {
    UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write(String.valueOf(ServletUtils.checkCredentialsEncoded(req, userService) ? 1 : 0));
    }
}
