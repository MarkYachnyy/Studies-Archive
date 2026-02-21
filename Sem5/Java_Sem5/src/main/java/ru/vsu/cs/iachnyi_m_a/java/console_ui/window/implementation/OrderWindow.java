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
import ru.vsu.cs.iachnyi_m_a.java.entity.order.Order;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItem;
import ru.vsu.cs.iachnyi_m_a.java.service.OrderService;
import ru.vsu.cs.iachnyi_m_a.java.service.ProductService;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderWindow implements Window {

    private Command commandOpenAllProductsWindow;
    private Command commandOpenProductPageWindow;
    private Command commandOpenAllOrdersWindow;

    private OrderService orderService;
    private UserService userService;
    private ProductService productService;

    private User user;
    private Order order;

    private TextLabel TextLabelHeader;
    private SelectItemPageList<OrderItem> SelectItemPageListOrderItems;
    private TextLabel TextLabelOrderStatus;

    public OrderWindow(ConsoleInterfaceApp app, Map<String, Object> params) {

        orderService = ApplicationContextProvider.getContext().getBean(OrderService.class);
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);

        user = userService.findUserById((Long) params.get("userId"));
        order = orderService.getOrderById((Long) params.get("orderId"));

        int orderSum = order.getItems().stream().mapToInt(oi -> oi.getPrice() * oi.getAmount()).reduce(Integer::sum).orElse(0);
        TextLabelHeader = new TextLabel(String.format("Заказ №%s от %s на сумму %s", order.getId(), order.getDate().toString(), orderSum));
        TextLabelOrderStatus = new TextLabel("Заказ в сборке");
        SelectItemPageListOrderItems = new SelectItemPageList<>(5, order.getItems(),
                oi -> String.format("%s | %s шт. х %s", productService.getProductById(oi.getId().getProductId()).getName(), oi.getAmount(), oi.getPrice()), true);

        commandOpenAllProductsWindow = new Command() {
            @Override
            public String getName() {
                return "Вернуться ко всем товарам";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.ALL_PRODUCTS, params);
            }
        };

        commandOpenProductPageWindow = new Command() {
            @Override
            public String getName() {
                return "Открыть страницу товара";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                params.put("productId", SelectItemPageListOrderItems.getSelectedItem().getId().getProductId());
                app.setCurrentWindow(WindowType.PRODUCT, params);
            }
        };

        commandOpenAllOrdersWindow = new Command() {
            @Override
            public String getName() {
                return "Назад ко всем заказам";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.ALL_ORDERS, params);
            }
        };
    }

    @Override
    public List<Command> getCommands() {
        return List.of(commandOpenAllProductsWindow, SelectItemPageListOrderItems.getSelectDownCommand(), SelectItemPageListOrderItems.getSelectUpCommand(),
                SelectItemPageListOrderItems.getSelectPreviousPageCommand(), SelectItemPageListOrderItems.getSelectNextPageCommand(), commandOpenProductPageWindow, commandOpenAllOrdersWindow);
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(TextLabelHeader, SelectItemPageListOrderItems, TextLabelOrderStatus);
    }

    @Override
    public WindowInputState getInputState() {
        return WindowInputState.COMMAND;
    }

    @Override
    public void acceptInputValue(String value) {

    }
}
