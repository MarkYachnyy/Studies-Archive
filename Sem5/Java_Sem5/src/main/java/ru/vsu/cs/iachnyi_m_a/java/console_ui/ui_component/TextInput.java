package ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextInput implements ConsoleUIComponent {

    public TextInput(String name) {
        this.name = name;
    }


    private String name;
    private String value;


    @Override
    public String getDrawableContent() {
        return String.format("%s: %s", name, value == null ? "_________" : value);
    }
}
