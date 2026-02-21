package ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component;

import lombok.Getter;
import lombok.Setter;

public class TextLabel implements ConsoleUIComponent {

    @Getter
    @Setter
    private String text;

    public TextLabel(String text) {
        this.text = text;
    }

    @Override
    public String getDrawableContent() {
        return text;
    }

}
