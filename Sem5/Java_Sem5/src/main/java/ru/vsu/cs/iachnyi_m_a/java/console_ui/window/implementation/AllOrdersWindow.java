package ru.vsu.cs.iachnyi_m_a.java.console_ui.window.implementation;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.ConsoleInterfaceApp;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.command.Command;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.ConsoleUIComponent;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.SelectItemPageList;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.TextLabel;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.Window;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowInputState;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowType;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.Order;
import ru.vsu.cs.iachnyi_m_a.java.service.OrderService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllOrdersWindow implements Window {

    private TextLabel TextLabelHeader;
    private SelectItemPageList<Order> SelectItemPageListOrders;

    private List<Order> orders;

    private OrderService orderService;
    private long userId;

    private Command commandOpenAllProductsWindow;
    private Command commandOpenOrderWindow;

    public AllOrdersWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        orderService = ApplicationContextProvider.getContext().getBean(OrderService.class);
        userId = (Long) params.get("userId");
        orders = orderService.getAllOrdersByUserId(userId);

        TextLabelHeader = new TextLabel("Заказы пользователя");
        SelectItemPageListOrders = new SelectItemPageList<>(5, orders, o -> String.format("Заказ №%s от %s", o.getId(), o.getDate()), true);

        commandOpenAllProductsWindow = new Command() {
            @Override
            public String getName() {
                return "Назад ко всем товарам";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", userId);
                app.setCurrentWindow(WindowType.ALL_PRODUCTS, params);
            }
        };

        commandOpenOrderWindow = new Command() {
            @Override
            public String getName() {
                return "Просмотреть заказ";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", userId);
                params.put("orderId", SelectItemPageListOrders.getSelectedItem().getId());
                app.setCurrentWindow(WindowType.ORDER, params);
            }
        };
    }

    @Override
    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<>();
        commands.add(commandOpenAllProductsWindow);
        if(!orders.isEmpty()){
            commands.addAll(List.of(SelectItemPageListOrders.getSelectDownCommand(), SelectItemPageListOrders.getSelectUpCommand(),
                    SelectItemPageListOrders.getSelectPreviousPageCommand(), SelectItemPageListOrders.getSelectNextPageCommand(),
                    commandOpenOrderWindow));
        }
        return commands;
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(TextLabelHeader, orders.isEmpty() ? new TextLabel("Заказов нет") : SelectItemPageListOrders);
    }

    @Override
    public WindowInputState getInputState() {
        return WindowInputState.COMMAND;
    }

    @Override
    public void acceptInputValue(String value) {

    }
}
