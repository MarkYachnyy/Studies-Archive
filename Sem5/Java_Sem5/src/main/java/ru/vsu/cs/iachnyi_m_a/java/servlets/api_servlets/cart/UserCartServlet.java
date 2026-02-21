package ru.vsu.cs.iachnyi_m_a.java.servlets.api_servlets.cart;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.service.CartService;
import ru.vsu.cs.iachnyi_m_a.java.service.ProductService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;
import ru.vsu.cs.iachnyi_m_a.java.servlets.ServletUtils;
import ru.vsu.cs.iachnyi_m_a.java.servlets.response_entity.CartItemResponseEntity;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/cart")
public class UserCartServlet extends HttpServlet {

    private UserService userService;
    private CartService cartService;
    private ProductService productService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        gson = new Gson();
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        cartService = ApplicationContextProvider.getContext().getBean(CartService.class);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!ServletUtils.checkCredentialsEncoded(req, userService)){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            List<CartItem> items = cartService.getCartOfUser(userService.findUserByEmail(req.getHeader("email")).getId());
            List<CartItemResponseEntity> res = items.stream().map(i -> new CartItemResponseEntity(i.getId().getProductId(),
                    productService.getProductById(i.getId().getProductId()).getName(), i.getQuantity(),
                    productService.getProductById(i.getId().getProductId()).getPrice())).toList();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print(gson.toJson(res));
        }
    }
}
