package ru.vsu.cs.iachnyi_m_a.java;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.ConsoleInterfaceApp;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.context.MyApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContextProvider.setContext(new MyApplicationContext());
        ConsoleInterfaceApp app = new ConsoleInterfaceApp(System.in, System.out);
        app.run();
    }
}