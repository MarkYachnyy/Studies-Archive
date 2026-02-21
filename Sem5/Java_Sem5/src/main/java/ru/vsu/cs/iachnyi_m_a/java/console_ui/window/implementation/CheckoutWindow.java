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
import ru.vsu.cs.iachnyi_m_a.java.entity.PickupPoint;
import ru.vsu.cs.iachnyi_m_a.java.entity.Product;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.Order;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItemId;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderStatus;
import ru.vsu.cs.iachnyi_m_a.java.service.*;

import java.util.*;

public class CheckoutWindow implements Window {

    private UserService userService;
    private CartService cartService;
    private ProductService productService;
    private OrderService orderService;
    private PickupPointService pickupPointService;

    private User user;
    private List<CartItem> cart;

    private TextLabel TextLabelHeader;
    private SelectItemPageList<CartItem> SelectItemPageListCart;
    private TextLabel TextLabelTotalPrice;

    private TextLabel TextLabelPickupPointInfo;
    private SelectItemPageList<PickupPoint> SelectItemPageListPickupPoints;
    private boolean choosingPickupPoint = false;

    private Command commandOpenCartWindow;
    private Command commandMakeOrder;
    private Command commandOpenPickupPointList;
    private Command commandSelectPickupPoint;

    public CheckoutWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);
        cartService = ApplicationContextProvider.getContext().getBean(CartService.class);
        productService = ApplicationContextProvider.getContext().getBean(ProductService.class);
        orderService = ApplicationContextProvider.getContext().getBean(OrderService.class);
        pickupPointService = ApplicationContextProvider.getContext().getBean(PickupPointService.class);

        user = params.get("userId") == null ? null : userService.findUserById((Long) params.get("userId"));
        if (user != null) {
            cart = cartService.getCartOfUser(user.getId());
        }

        TextLabelHeader = new TextLabel("Оформить заказ");
        SelectItemPageListCart = new SelectItemPageList<>(5, cart, ci -> {
            Product p = productService.getProductById(ci.getId().getProductId());
            return String.format("%s | %s шт. : %s₽", p.getName(), ci.getQuantity(), ci.getQuantity() * p.getPrice());
        }, false);
        TextLabelTotalPrice = new TextLabel(String.format("Итого: %s₽", cart.stream().
                mapToInt(ci -> ci.getQuantity() * productService.getProductById(ci.getId().getProductId()).getPrice()).reduce(Integer::sum).orElse(0)));

        SelectItemPageListPickupPoints = new SelectItemPageList<>(3, pickupPointService.getAllPickupPoints(), pp -> String.format("Пункт выдачи на %s", pp.getAddress()), true);
        TextLabelPickupPointInfo = new TextLabel(String.format("Пункт выдачи на %s", SelectItemPageListPickupPoints.getSelectedItem().getAddress()));

        commandOpenCartWindow = new Command() {
            @Override
            public String getName() {
                return "Обратно в корзину";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.CART, params);
            }
        };

        commandMakeOrder = new Command() {
            @Override
            public String getName() {
                return "Оформить заказ";
            }

            @Override
            public void execute() {
                Order toInsert = new Order(0, user.getId(), new Date(), SelectItemPageListPickupPoints.getSelectedItem().getId(), OrderStatus.ASSEMBLY, null);
                toInsert.setItems(cart.stream().map(ci -> new OrderItem(new OrderItemId(0, ci.getId().getProductId()),
                        ci.getQuantity(), productService.getProductById(ci.getId().getProductId()).getPrice())).toList());
                Order res = orderService.addOrder(toInsert);
                for(CartItem cartItem : cart) {
                    Product product = productService.getProductById(cartItem.getId().getProductId());
                    product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                    productService.updateProduct(product);
                }
                for(CartItem cartItem : cart) {
                    cartService.deleteCartItem(cartItem.getId());
                }
                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getId());
                params.put("orderId", res.getId());
                app.setCurrentWindow(WindowType.ORDER, params);
            }
        };

        commandOpenPickupPointList = new Command() {
            @Override
            public String getName() {
                return "Изменить пункт выдачи";
            }

            @Override
            public void execute() {
                choosingPickupPoint = true;
            }
        };

        commandSelectPickupPoint = new Command() {
            @Override
            public String getName() {
                return "Выбрать пункт выдачи";
            }

            @Override
            public void execute() {
                choosingPickupPoint = false;
                TextLabelPickupPointInfo = new TextLabel(String.format("Пункт выдачи на %s", SelectItemPageListPickupPoints.getSelectedItem().getAddress()));
            }
        };
    }

    @Override
    public List<Command> getCommands() {
        List<Command> res = new ArrayList<>(List.of(SelectItemPageListCart.getSelectPreviousPageCommand(), SelectItemPageListCart.getSelectNextPageCommand(), commandOpenCartWindow));
        if(choosingPickupPoint) {
            res.addAll(List.of(SelectItemPageListPickupPoints.getSelectUpCommand(), SelectItemPageListPickupPoints.getSelectDownCommand(),
                    SelectItemPageListPickupPoints.getSelectPreviousPageCommand(), SelectItemPageListPickupPoints.getSelectNextPageCommand(),
                    commandSelectPickupPoint));
        } else {
            res.add(commandOpenPickupPointList);
            res.add(commandMakeOrder);
        }
        return res;
    }

    @Override
    public WindowInputState getInputState() {
        return WindowInputState.COMMAND;
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(TextLabelHeader, SelectItemPageListCart, TextLabelTotalPrice, choosingPickupPoint ? SelectItemPageListPickupPoints : TextLabelPickupPointInfo);
    }

    @Override
    public void acceptInputValue(String value) {

    }
}
