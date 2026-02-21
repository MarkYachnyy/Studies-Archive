package ru.vsu.cs.iachnyi_m_a.java.servlets.page_servlets;

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

@WebServlet(urlPatterns = "/product")
public class ProductPageServlet extends HttpServlet {
    private ProductService productService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id_str  = req.getParameter("id");
        if(id_str == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Product product = productService.getProductById(id);
            if (product == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                req.getRequestDispatcher("/WEB-INF/templates/product.html").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
