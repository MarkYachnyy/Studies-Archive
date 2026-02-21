package ru.vsu.cs.iachnyi_m_a.java.console_ui.window;

import ru.vsu.cs.iachnyi_m_a.java.console_ui.command.Command;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component.ConsoleUIComponent;

import java.util.List;

public interface Window {
    List<Command> getCommands();
    List<ConsoleUIComponent> getComponents();
    WindowInputState getInputState();
    void acceptInputValue(String value);
}
