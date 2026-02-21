package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets.cart;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.service.CartService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;
import ru.vsu.cs.iachnyi_m_a.java.servlets.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/api/cart/remove")
public class RemoveFromCartServlet extends HttpServlet {
    private UserService userService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        cartService = ApplicationContextProvider.getContext().getBean(CartService.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!ServletUtils.checkCredentialsEncoded(req, userService)){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            User user = userService.findUserByEmail(req.getHeader("email"));
            try{
                long productId = Long.parseLong(req.getParameter("productId"));
                CartItem cartItem = cartService.getCartItemByUserIdAndProductId(user.getId(), productId);
                if(cartItem != null){
                    if(cartItem.getQuantity() == 1){
                        cartService.deleteCartItem(cartItem.getId());
                    } else if(cartItem.getQuantity() == 0) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        cartService.saveCartItem(cartItem);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
