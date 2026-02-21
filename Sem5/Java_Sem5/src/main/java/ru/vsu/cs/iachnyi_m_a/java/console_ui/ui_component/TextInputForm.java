package ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextInputForm implements ConsoleUIComponent{

    private List<TextInput> textInputs;
    private Integer selectedInputIndex;

    public TextInputForm(String... inputNames) {
        textInputs = new ArrayList<TextInput>();
        selectedInputIndex = -1;
        for (String inputName : inputNames) {
            textInputs.add(new TextInput(inputName));
        }
    }

    public List<String> getInputValues(){
        return textInputs.stream().map(TextInput::getValue).toList();
    }

    public void deselectInput(){
        selectedInputIndex = -1;
    }

    public void setInputIndex(int inputIndex){
        if(inputIndex >= textInputs.size()){
            throw new IllegalArgumentException();
        }
        selectedInputIndex = inputIndex;
    }

    public void clearInputValues(){
        for (TextInput textInput : textInputs) {
            textInput.setValue(null);
        }
    }

    public int getInputCount(){
        return textInputs.size();
    }

    public String getInputName(int index){
        return textInputs.get(index).getName();
    }

    private void setSelectedInputIndex(Integer selectedInputIndex) {
        this.selectedInputIndex = selectedInputIndex;
    }

    public void acceptInputValue(String inputValue) {
        textInputs.get(selectedInputIndex).setValue(inputValue);
    }

    @Override
    public String getDrawableContent() {
        return IntStream.range(0, textInputs.size()).mapToObj(i -> {
            TextInput textInput = textInputs.get(i);
            return i == selectedInputIndex ? ("\033[43m" + textInput.getDrawableContent() + "\033[0m") : textInput.getDrawableContent();
        }).collect(Collectors.joining("\n"));
    }
}
