package ru.vsu.cs.iachnyi_m_a.java.console_ui.window.implementation;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.ConsoleInterfaceApp;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.command.Command;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.ConsoleUIComponent;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.TextLabel;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowInputState;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.Window;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowType;
import ru.vsu.cs.iachnyi_m_a.java.entity.Product;
import ru.vsu.cs.iachnyi_m_a.java.entity.Seller;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItemId;
import ru.vsu.cs.iachnyi_m_a.java.service.CartService;
import ru.vsu.cs.iachnyi_m_a.java.service.ProductService;
import ru.vsu.cs.iachnyi_m_a.java.service.SellerService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductWindow implements Window {

    private SellerService sellerService;
    private ProductService productService;
    private UserService userService;
    private CartService cartService;

    private User user;
    private CartItem cartItem;
    private Product product;
    private Seller seller;

    private Command commandAddToCart;
    private Command commandRemoveFromCart;
    private Command commandOpenCartWindow;
    private Command commandOpenSellerProfileWindow;
    private Command commandOpenAllProductsWindow;
    private Command commandLoginLogout;

    private TextLabel TextLabelHeader;
    private TextLabel TextLabelProductInfo;
    private TextLabel TextLabelInCart;

    public ProductWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        sellerService = ApplicationContextProvider.getContext().getBean(SellerService.class);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);
        cartService = ApplicationContextProvider.getContext().getBean(CartService.class);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        product = productService.getProductById((Long) params.get("productId"));
        seller = sellerService.getSellerById(product.getSellerId());
        user = params.get("userId") == null ? null : userService.findUserById((Long) params.get("userId"));
        if(user != null) {
            cartItem = cartService.getCartItemByUserIdAndProductId(user.getId(), product.getId());
            if (cartItem == null) cartItem = new CartItem(new CartItemId(user.getId(), product.getId()), 0);
        }
        TextLabelHeader = new TextLabel(String.format("Товар %s", product.getName()));
        TextLabelProductInfo = new TextLabel(String.format("""
                        Продавец %s
                        В наличии: %d
                        """
                ,seller.getName(), product.getStockQuantity()));
        TextLabelInCart = new TextLabel(String.format("В корзине: - %s +", cartItem == null ? 0 : cartItem.getQuantity()));

        commandOpenSellerProfileWindow = new Command() {
            @Override
            public String getName() {
                return "Открыть профиль продавца";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("sellerId", product.getSellerId());
                if(user != null) params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.SELLER_PROFILE, params);
            }
        };

        commandOpenAllProductsWindow = new Command() {
            @Override
            public String getName() {
                return "Вернуться ко всем товарам";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                if(user != null) params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.ALL_PRODUCTS, params);
            }
        };

        commandLoginLogout = new Command() {
            @Override
            public String getName() {
                return user == null ? "Войти в аккаунт" : String.format("Выполнен вход под именем %s. Выберите, чтобы выйти", user.getName());
            }

            @Override
            public void execute() {
                if (user == null) {
                    app.setCurrentWindow(WindowType.LOGIN, new HashMap<>());
                } else {
                    user = null;
                    cartItem = null;
                }
            }
        };

        commandAddToCart = new Command() {
            @Override
            public String getName() {
                return "Добавить товар в корзину";
            }

            @Override
            public void execute() {
                if(user == null) {
                    commandLoginLogout.execute();
                } else {
                    if(cartItem.getQuantity() < product.getStockQuantity()) {
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                        cartService.saveCartItem(cartItem);
                        Map<String, Object> params = new HashMap<>();
                        params.put("userId", user.getId());
                        params.put("productId", product.getId());
                        app.setCurrentWindow(WindowType.PRODUCT, params);
                    }
                }
            }
        };

        commandRemoveFromCart = new Command() {
            @Override
            public String getName() {
                return "Убрать товар из корзины";
            }

            @Override
            public void execute() {
                if(user == null) {
                    commandLoginLogout.execute();
                } else {
                    if(cartItem.getQuantity() > 1) {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        cartService.saveCartItem(cartItem);
                    } else if(cartItem.getQuantity() == 1){
                        cartService.deleteCartItem(cartItem.getId());
                    }
                    Map<String, Object> params = new HashMap<>();
                    params.put("userId", user.getId());
                    params.put("productId", product.getId());
                    app.setCurrentWindow(WindowType.PRODUCT, params);
                }
            }
        };

        commandOpenCartWindow = new Command() {
            @Override
            public String getName() {
                return "Открыть корзину";
            }

            @Override
            public void execute() {
                if (user == null) {
                    commandLoginLogout.execute();
                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("userId", user.getId());
                    app.setCurrentWindow(WindowType.CART, params);
                }
            }
        };
    }

    @Override
    public List<Command> getCommands() {
        return List.of(commandOpenSellerProfileWindow, commandOpenAllProductsWindow, commandAddToCart, commandRemoveFromCart, commandOpenCartWindow, commandLoginLogout);
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(TextLabelHeader, TextLabelProductInfo, TextLabelInCart);
    }

    @Override
    public WindowInputState getInputState() {
        return WindowInputState.COMMAND;
    }

    @Override
    public void acceptInputValue(String value) {

    }
}
