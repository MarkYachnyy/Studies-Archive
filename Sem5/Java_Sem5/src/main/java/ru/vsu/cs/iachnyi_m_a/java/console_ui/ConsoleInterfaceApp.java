package ru.vsu.cs.iachnyi_m_a.java.console_ui;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.command.Command;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.ConsoleUIComponent;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowInputState;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.Window;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowFactory;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.window.WindowType;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleInterfaceApp {

    public static final int SEPARATOR_DASH_COUNT = 40;

    private WindowFactory windowFactory;
    private Window currentWindow;
    private boolean running = false;
    private Scanner scanner;
    private PrintStream outputStream;

    public ConsoleInterfaceApp(InputStream inputStream, PrintStream outputStream) {
        this.windowFactory = new WindowFactory(this);
        this.currentWindow = windowFactory.createWindow(WindowType.ALL_PRODUCTS, new HashMap<>());
        this.scanner = new Scanner(inputStream);
        this.outputStream = outputStream;
    }

    public void run() {
        running = true;
        while (running) {

            outputStream.print("\033[H\033[2J");
            outputStream.flush();

            try {
                outputStream.println(currentWindow.getComponents().stream().map(ConsoleUIComponent::getDrawableContent).collect(Collectors.joining('\n'+"-".repeat(SEPARATOR_DASH_COUNT) + '\n')));
            } catch (Exception e) {
                System.err.println("WINDOW: " + currentWindow.getClass().getName());
            }

            System.out.println("-".repeat(SEPARATOR_DASH_COUNT));
            if (currentWindow.getInputState() == WindowInputState.COMMAND) {
                Map<String, Command> commands = new HashMap<>();
                for (int i = 0; i < currentWindow.getCommands().size(); i++) {
                    commands.put(String.valueOf(i + 1), currentWindow.getCommands().get(i));
                    System.out.printf("|[%d] %s%n", i + 1, currentWindow.getCommands().get(i).getName());
                }
                System.out.println("-".repeat(SEPARATOR_DASH_COUNT));
                System.out.print("Введите команду: ");
                String commandKey = scanner.nextLine().strip();
                Command command = commands.get(commandKey);
                if (command != null) {
                    command.execute();
                } else {
                    System.out.println("Выбрана неверная команда");
                }
            } else {
                System.out.print("Введите значение поля:");
                currentWindow.acceptInputValue(scanner.nextLine().strip());
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void setCurrentWindow(WindowType type, Map<String, Object> params) {
        this.currentWindow = windowFactory.createWindow(type, params);
    }

}
