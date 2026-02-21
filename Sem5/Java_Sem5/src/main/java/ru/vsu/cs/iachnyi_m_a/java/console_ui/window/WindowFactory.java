package ru.vsu.cs.iachnyi_m_a.java.console_ui.window;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.ConsoleInterfaceApp;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.implementation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WindowFactory {


    private HashMap<WindowType, Function<Map<String, Object>, Window>> windows = new HashMap<>();

    public WindowFactory(ConsoleInterfaceApp app) {
        windows.put(WindowType.CART, params -> new CartWindow(app, params));
        windows.put(WindowType.ALL_PRODUCTS, params -> new AllProductsWindow(app, params));
        windows.put(WindowType.ORDER, params -> new OrderWindow(app, params));
        windows.put(WindowType.PRODUCT, params -> new ProductWindow(app, params));
        windows.put(WindowType.SELLER_PROFILE, params -> new SellerProfileWindow(app, params));
        windows.put(WindowType.REGISTER, params -> new RegisterWindow(app, params));
        windows.put(WindowType.LOGIN, params -> new LoginWindow(app, params));
        windows.put(WindowType.CHECKOUT, params -> new CheckoutWindow(app, params));
        windows.put(WindowType.ALL_ORDERS, params -> new AllOrdersWindow(app, params));
    }

    public Window createWindow(WindowType type, Map<String, Object> params) {
        return windows.get(type).apply(params);
    }
}
