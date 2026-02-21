package ru.vsu.cs.iachnyi_m_a.java.servlets.page_servlets;

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

@WebServlet("/cart")
public class CartPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/templates/cart.html").forward(req, resp);
    }
}
