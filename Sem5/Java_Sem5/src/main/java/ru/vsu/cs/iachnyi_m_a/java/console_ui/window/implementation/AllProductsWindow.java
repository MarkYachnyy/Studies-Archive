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
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.service.ProductService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AllProductsWindow implements Window {

    private WindowInputState inputState;
    private ProductService productService;

    private TextLabel LabelTitle;
    private SelectItemPageList<Product> SelectItemPageListProduct;

    private Command commandOpenProductWindow;
    private Command commandLoginLogout;
    private Command commandOpenCartWindow;
    private Command commandOpenAllOrdersWindow;

    private UserService userService;
    private User user;

    public AllProductsWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);

        user = params.get("userId") == null ? null : userService.findUserById((Long) params.get("userId"));
        inputState = WindowInputState.COMMAND;

        LabelTitle = new TextLabel("Все товары");
        SelectItemPageListProduct = new SelectItemPageList<>(5, productService.getAllProducts().stream().filter(product -> product.getStockQuantity() > 0).collect(Collectors.toList()),
                product -> product.getName() + ": " + product.getPrice() + " | " + product.getStockQuantity() + " шт. в наличии", true);

        commandOpenProductWindow = new Command() {
            @Override
            public String getName() {
                return "Выбрать товар";
            }

            @Override
            public void execute() {
                Long productId = SelectItemPageListProduct.getSelectedItem().getId();
                HashMap<String, Object> params = new HashMap<>();
                params.put("productId", productId);
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
                if(user == null) {
                    commandLoginLogout.execute();
                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("userId", user.getId());
                    app.setCurrentWindow(WindowType.CART, params);
                }
            }
        };

        commandOpenAllOrdersWindow = new Command() {
            @Override
            public String getName() {
                return "Открыть все заказы";
            }

            @Override
            public void execute() {
                if(user == null) {
                    commandLoginLogout.execute();
                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("userId", user.getId());
                    app.setCurrentWindow(WindowType.ALL_ORDERS, params);
                }
            }
        };
    }

    @Override
    public List<Command> getCommands() {

        return List.of(SelectItemPageListProduct.getSelectUpCommand(), SelectItemPageListProduct.getSelectDownCommand(),
                SelectItemPageListProduct.getSelectNextPageCommand(),
                SelectItemPageListProduct.getSelectPreviousPageCommand(), commandOpenProductWindow, commandOpenCartWindow, commandOpenAllOrdersWindow, commandLoginLogout);
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(LabelTitle, SelectItemPageListProduct);
    }

    @Override
    public WindowInputState getInputState() {
        return inputState;
    }

    @Override
    public void acceptInputValue(String value) {
        if (inputState == WindowInputState.COMMAND) {
            throw new IllegalStateException("Trying to pass input value while the window is in COMMAND input state");
        }
    }
}
