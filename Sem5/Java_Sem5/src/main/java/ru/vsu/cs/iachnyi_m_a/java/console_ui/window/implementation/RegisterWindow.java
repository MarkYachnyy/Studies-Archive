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

import java.util.*;

public class RegisterWindow implements Window {

    private TextLabel TextLabelHeader;
    private TextInputForm TextInputFormUserData;
    private TextLabel TextLabelStatus;

    private Command commandConfirmRegister;
    private Command commandOpenLoginWindow;

    private WindowInputState inputState;

    private UserService userService;

    public RegisterWindow(ConsoleInterfaceApp app, Map<String, Object> params) {
        userService = ApplicationContextProvider.getContext().getBean(UserService.class);

        TextLabelHeader = new TextLabel("Зарегистрировать аккаунт");
        TextLabelStatus = new TextLabel("");
        TextInputFormUserData = new TextInputForm("Имя", "Почта", "Пароль", "Подтверждение пароля");

        inputState = WindowInputState.COMMAND;

        commandConfirmRegister = new Command() {
            @Override
            public String getName() {
                return "Зарегистрироваться";
            }

            @Override
            public void execute() {
                List<String> inputValues = TextInputFormUserData.getInputValues();
                String nameValue = inputValues.get(0);
                String emailValue = inputValues.get(1);
                String passwordValue = inputValues.get(2);
                String passwordConfirmValue = inputValues.get(3);
                if(nameValue == null || emailValue == null || passwordValue == null || passwordConfirmValue == null) {
                    TextLabelStatus.setText("Заполнены не все поля");
                } else if (!Objects.equals(passwordValue, passwordConfirmValue)){
                    TextLabelStatus.setText("Пароли не совпадают");
                } else {
                    User existing = userService.findUserByEmail(emailValue);
                    if (existing != null) {
                        TextLabelStatus.setText("Пользователь с таким email существует");
                    } else {
                        userService.registerUser(new User(0, nameValue, emailValue, passwordValue));
                        TextInputFormUserData.clearInputValues();
                        TextLabelStatus.setText("Пользователь успешно зарегистрирован");
                    }
                }
            }
        };
        commandOpenLoginWindow = new Command() {
            @Override
            public String getName() {
                return "Войти в аккаунт";
            }

            @Override
            public void execute() {
                app.setCurrentWindow(WindowType.LOGIN, new HashMap<>());
            }
        };
    }

    @Override
    public List<Command> getCommands() {
        List<Command> res = new ArrayList<>();
        for (int i = 0; i < TextInputFormUserData.getInputCount(); i++) {
            int thisI = i;
            res.add(new Command() {
                @Override
                public String getName() {
                    return String.format("Ввести поле %s", TextInputFormUserData.getInputName(thisI));
                }

                @Override
                public void execute() {
                    TextInputFormUserData.setInputIndex(thisI);
                    RegisterWindow.this.inputState = WindowInputState.VALUE;
                }
            });
        }
        res.add(commandConfirmRegister);
        res.add(commandOpenLoginWindow);
        return res;
    }

    @Override
    public List<ConsoleUIComponent> getComponents() {
        return List.of(TextLabelHeader, TextInputFormUserData, TextLabelStatus);
    }

    @Override
    public WindowInputState getInputState() {
        return inputState;
    }

    @Override
    public void acceptInputValue(String value) {
        if(!"".equals(value)) {
            TextInputFormUserData.acceptInputValue(value);
        }
        inputState = WindowInputState.COMMAND;
        TextInputFormUserData.deselectInput();
    }


}
