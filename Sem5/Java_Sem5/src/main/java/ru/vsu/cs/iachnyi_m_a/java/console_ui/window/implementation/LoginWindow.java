package ru.vsu.cs.iachnyi_m_a.java.console_ui.window.implementation;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.ConsoleInterfaceApp;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.command.Command;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.ConsoleUIComponent;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.TextInputForm;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.TextLabel;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowInputState;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.Window;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowType;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginWindow implements Window {

    private TextLabel TextLabelHeader;
    private TextInputForm TextInputFormLoginData;
    private TextLabel TextLabelStatus;
    private User user;

    private WindowInputState inputState;

    private Command commandConfirmLogin;
    private Command commandOpenRegisterWindow;
    private Command commandOpenAllProductsWindow;

    private UserService userService;

    public LoginWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        inputState = WindowInputState.COMMAND;
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);

        TextInputFormLoginData = new TextInputForm("Почта", "Пароль");
        TextLabelHeader = new TextLabel("Войти в аккаунт");
        TextLabelStatus = new TextLabel("");

        commandConfirmLogin = new Command() {

            @Override
            public String getName() {
                return "Войти";
            }

            @Override
            public void execute() {
                List<String> InputValues = TextInputFormLoginData.getInputValues();
                String emailValue = InputValues.get(0);
                String passwordValue = InputValues.get(1);
                if (emailValue == null || passwordValue == null) {
                    TextLabelStatus.setText("Не все поля заполнены");
                } else {
                    User existing = userService.findUserByEmail(emailValue);
                    if (existing == null || !existing.getPassword().equals(passwordValue)) {
                        TextLabelStatus.setText("Неверная почта или пароль");
                    } else {
                        TextInputFormLoginData.clearInputValues();
                        TextLabelStatus.setText("Успешный вход в аккаунт под именем " + existing.getName());
                        user = existing;
                    }
                }
            }
        };
        commandOpenRegisterWindow = new Command() {

            @Override
            public String getName() {
                return "Нет аккаунта? Зарегистрироваться";
            }

            @Override
            public void execute() {
                app.setCurrentWindow(WindowType.REGISTER, new HashMap<>());
            }
        };
        commandOpenAllProductsWindow = new Command() {
            @Override
            public String getName() {
                return "Вернуться к списку товаров";
            }

            @Override
            public void execute() {
                Map<String, Object> params = new HashMap<>();
                if (user != null) params.put("userId", user.getId());
                app.setCurrentWindow(WindowType.ALL_PRODUCTS, params);
            }
        };
    }

    @Override
    public List<Command> getCommands() {
        List<Command> res = new ArrayList<>(/*commandConfirmLogin, commandOpenAllProductsWindow*/);
        for (int i = 0; i < TextInputFormLoginData.getInputCount(); i++) {
            int thisI = i;
            res.add(new Command() {
                @Override
                public String getName() {
                    return String.format("Ввести поле %s", TextInputFormLoginData.getInputName(thisI));
                }

                @Override
                public void execute() {
                    TextInputFormLoginData.setInputIndex(thisI);
                    LoginWindow.this.inputState = WindowInputState.VALUE;
                }
            });
        }
        res.add(commandConfirmLogin);
        res.add(commandOpenAllProductsWindow);
        if (user == null) res.add(commandOpenRegisterWindow);
        return res;
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(TextLabelHeader, TextInputFormLoginData, TextLabelStatus);
    }

    @Override
    public WindowInputState getInputState() {
        return inputState;
    }

    @Override
    public void acceptInputValue(String value) {
        if (!"".equals(value)) {
            TextInputFormLoginData.acceptInputValue(value);
        }
        inputState = WindowInputState.COMMAND;
        TextInputFormLoginData.deselectInput();
    }
}
