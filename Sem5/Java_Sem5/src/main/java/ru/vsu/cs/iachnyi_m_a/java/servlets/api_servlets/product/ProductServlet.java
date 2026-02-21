package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets.product;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.Product;
import ru.vsu.cs.iachnyi_m_a.java.service.ProductService;

import java.io.IOException;

@WebServlet("/api/product")
public class ProductServlet extends HttpServlet {
    private ProductService productService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id_str = req.getParameter("id");
        if(id_str==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                long id = Long.parseLong(id_str);
                Product product = productService.getProductById(id);
                if(product==null){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().print(gson.toJson(product));
                    resp.getWriter().flush();
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
