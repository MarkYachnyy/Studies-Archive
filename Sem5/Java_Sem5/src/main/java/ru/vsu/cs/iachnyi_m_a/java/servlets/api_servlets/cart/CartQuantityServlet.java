package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets.cart;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContext;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.service.CartService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;
import ru.vsu.cs.iachnyi_m_a.java.servlets.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/api/cart/quantity")
public class CartQuantityServlet extends HttpServlet {
    private UserService userService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = ApplicationContextProvider.getContext().getBean(CartService.class);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(ServletUtils.checkCredentialsEncoded(req, userService)){
            Long userId = userService.findUserByEmail(req.getHeader("email")).getId();
            String productIdStr = req.getParameter("productId");
            if(productIdStr == null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try{
                Long productId = Long.parseLong(productIdStr);
                CartItem ci = cartService.getCartItemByUserIdAndProductId(userId, productId);
                resp.setContentType("text/plain");
                resp.setCharacterEncoding("UTF-8");
                if(ci == null){
                    resp.getWriter().println(0);
                } else {
                    resp.getWriter().println(ci.getQuantity());
                }
                resp.getWriter().flush();
            } catch (NumberFormatException e){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }
}
