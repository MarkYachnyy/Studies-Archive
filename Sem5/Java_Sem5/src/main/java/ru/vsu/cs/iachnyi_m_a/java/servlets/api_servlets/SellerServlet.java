package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.Seller;
import ru.vsu.cs.iachnyi_m_a.java.service.SellerService;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(urlPatterns = "/api/seller")
public class SellerServlet extends HttpServlet {

    private SellerService sellerService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        sellerService = ApplicationContextProvider.getContext().getBean(SellerService.class);
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id_str = req.getParameter("id");
        if(id_str == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                long id = Long.parseLong(id_str);
                Seller seller = sellerService.getSellerById(id);
                if(seller == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().print(gson.toJson(seller));
                    resp.getWriter().flush();
                }
            } catch (NumberFormatException e){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
