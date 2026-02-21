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
import ru.vsu.cs.iachnyi_m_a.java.entity.Product;
import ru.vsu.cs.iachnyi_m_a.java.entity.Seller;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.service.ProductService;
import ru.vsu.cs.iachnyi_m_a.java.service.SellerService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerProfileWindow implements Window {

    private TextLabel TextLabelHeader;
    private SelectItemPageList<Product> ListProducts;

    private SellerService sellerService;
    private ProductService productService;
    private UserService userService;

    private Command commandOpenProduct;
    private Command commandLoginLogout;
    private Command commandOpenCartWindow;

    private Seller seller;
    private User user;

    public SellerProfileWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        sellerService = ApplicationContextProvider.getContext().getBean(SellerService.class);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);

        seller = sellerService.getSellerById((Long) params.get("sellerId"));
        user = params.get("userId") == null ? null : userService.findUserById((Long) params.get("userId"));
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);

        TextLabelHeader = new TextLabel("Профиль продавца " + seller.getName());
        ListProducts = new SelectItemPageList<>(5, productService.getAllProductsOfSeller(seller.getId()),
                product -> product.getName() + ": " + product.getPrice() + " | " + product.getStockQuantity() + " шт. в наличии", true);

        commandOpenProduct = new Command() {
            @Override
            public String getName() {
                return "Выбрать товар";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("productId", ListProducts.getSelectedItem().getId());
                if (user != null) params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.PRODUCT, params);
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
                if(user == null){
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
        return List.of(ListProducts.getSelectDownCommand(), ListProducts.getSelectUpCommand(),
                ListProducts.getSelectPreviousPageCommand(), ListProducts.getSelectNextPageCommand(),
                commandOpenProduct, commandOpenCartWindow, commandLoginLogout);
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(TextLabelHeader, ListProducts);
    }

    @Override
    public WindowInputState getInputState() {
        return WindowInputState.COMMAND;
    }

    @Override
    public void acceptInputValue(String value) {

    }
}
