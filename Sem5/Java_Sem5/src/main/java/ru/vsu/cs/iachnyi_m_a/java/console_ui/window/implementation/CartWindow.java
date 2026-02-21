package ru.vsu.cs.iachnyi_m_a.java.console_ui.window.implementation;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.ConsoleInterfaceApp;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.command.Command;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.ConsoleUIComponent;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.SelectItemPageList;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.TextLabel;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowInputState;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.Window;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowType;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.service.CartService;
import ru.vsu.cs.iachnyi_m_a.java.service.ProductService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartWindow implements Window {

    private TextLabel LabelHeader;
    private SelectItemPageList<CartItem> ListItems;

    private UserService userService;
    private CartService cartService;
    private ProductService productService;

    private Command commandAddSelectedProductToCart;
    private Command commandRemoveSelectedProductFromCart;
    private Command commandOpenAllProductsWindow;
    private Command commandOpenCheckoutWindow;

    private User user;
    private List<CartItem> cartItems;

    public CartWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        cartService = ApplicationContextProvider.getContext().getBean(CartService.class);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);

        user = params.get("userId") == null ? null : userService.findUserById((Long) params.get("userId"));
        cartItems = user == null ? null : cartService.getCartOfUser(user.getId());

        ListItems = new SelectItemPageList<>(5, cartItems == null ? List.of() : cartItems,
                item -> productService.getProductById(item.getId().getProductId()).getName() + String.format(" | - %s +", item.getQuantity()), true);
        LabelHeader = new TextLabel("Корзина");

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

        commandAddSelectedProductToCart = new Command() {
            @Override
            public String getName() {
                return "Добавить еще единицу товара";
            }

            @Override
            public void execute() {
                CartItem item = ListItems.getSelectedItem();
                if(productService.getProductById(item.getId().getProductId()).getStockQuantity() > item.getQuantity()) item.setQuantity(item.getQuantity() + 1);
                cartService.saveCartItem(item);
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.CART, params);
            }
        };

        commandRemoveSelectedProductFromCart = new Command() {
            @Override
            public String getName() {
                return "Убрать единицу товара";
            }

            @Override
            public void execute() {
                CartItem item = ListItems.getSelectedItem();
                if(item.getQuantity() > 1){
                    item.setQuantity(item.getQuantity() - 1);
                    cartService.saveCartItem(item);
                } else {
                    cartService.deleteCartItem(item.getId());
                }
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.CART, params);
            }
        };

        commandOpenCheckoutWindow = new Command() {
            @Override
            public String getName() {
                return "К оформлению заказа";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.CHECKOUT, params);
            }
        };
    }

    @Override
    public List<Command> getCommands() {
        if(cartItems == null || cartItems.isEmpty()){
            return List.of(commandOpenAllProductsWindow);
        } else {
            return List.of(commandOpenAllProductsWindow, ListItems.getSelectDownCommand(), ListItems.getSelectUpCommand(),
                    ListItems.getSelectPreviousPageCommand(), ListItems.getSelectNextPageCommand(),
                    commandRemoveSelectedProductFromCart, commandAddSelectedProductToCart, commandOpenCheckoutWindow);
        }
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(LabelHeader, cartItems.isEmpty() ? new TextLabel("Корзина пуста") : ListItems);
    }

    @Override
    public WindowInputState getInputState() {
        return WindowInputState.COMMAND;
    }

    @Override
    public void acceptInputValue(String value) {

    }
}
