package ru.vsu.cs.iachnyi_m_a.java.console_ui.ui_component;

import lombok.Getter;
import lombok.NonNull;
import ru.vsu.cs.iachnyi_m_a.java.console_ui.command.Command;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SelectItemPageList<T> implements ConsoleUIComponent {

    private List<T> content;
    private int selectedIndex;
    private int page;
    private int itemsOnPage;
    private Function<T, String> adapter;
    @Getter
    private Command selectUpCommand;
    @Getter
    private Command selectDownCommand;
    @Getter
    private Command selectNextPageCommand;
    @Getter
    private Command selectPreviousPageCommand;

    public SelectItemPageList(int itemsOnPage, List<T> content, Function<T, String> adapter, boolean selectable) {
        this.itemsOnPage = itemsOnPage;
        this.content = content;
        this.adapter = adapter;
        this.selectedIndex = selectable ? 0 : -1;
        this.page = 0;
        int pageCount = content.size() / itemsOnPage + (content.size() % itemsOnPage == 0 ? 0 : 1);
        selectUpCommand = new Command() {
            @Override
            public String getName() {
                return "Наверх";
            }

            @Override
            public void execute() {
                if (content.isEmpty() || !selectable) return;
                if (selectedIndex == 0) {
                    if (page > 0) {
                        selectedIndex = itemsOnPage - 1;
                        page--;
                    }
                } else {
                    selectedIndex--;
                }
            }
        };
        selectDownCommand = new Command() {

            @Override
            public String getName() {
                return "Вниз";
            }

            @Override
            public void execute() {
                if(content.isEmpty() || !selectable) return;
                if(page == pageCount - 1 && selectedIndex == content.size() % itemsOnPage - 1) return;
                if (selectedIndex == itemsOnPage - 1) {
                    if (page < pageCount - 1) {
                        selectedIndex = 0;
                        page++;
                    }
                } else {
                    selectedIndex++;
                }
            }
        };
        selectPreviousPageCommand = new Command() {
            @Override
            public String getName() {
                return "Предыдущая страница";
            }

            @Override
            public void execute() {
                if(content.isEmpty()) return;
                if (page > 0) {
                    page--;
                    selectedIndex = selectable ? 0 : -1;
                }
            }
        };
        selectNextPageCommand = new Command() {
            @Override
            public String getName() {
                return "Следующая страница";
            }

            @Override
            public void execute() {
                if(content.isEmpty()) return;
                if (page < pageCount - 1) {
                    page++;
                    selectedIndex = selectable ? 0 : -1;
                }
            }
        };
    }

    public T getSelectedItem() {
        return content.isEmpty() ? null : content.get(selectedIndex + page * itemsOnPage);
    }

    @Override
    public String getDrawableContent() {
        int pageCount = content.size() / itemsOnPage + (content.size() % itemsOnPage == 0 ? 0 : 1);
        int itemsOnThisPage = content.size() % itemsOnPage == 0 ? itemsOnPage : content.size() % itemsOnPage;
        int finalIndex = page == pageCount - 1 ? page * itemsOnPage + itemsOnThisPage : (page + 1) * itemsOnPage;
        int blankLines = itemsOnPage - itemsOnThisPage;
        String res = IntStream.range(page * itemsOnPage, finalIndex).mapToObj(i -> {
            if (i % itemsOnPage == selectedIndex) {
                return "\033[43m" + adapter.apply(content.get(i)) + " \033[0m";
            } else {
                return adapter.apply(content.get(i));
            }
        }).collect(Collectors.joining("\n"));
        res += "\n".repeat(blankLines + 2);
        res += String.format("Страница %d из %d", page + 1, pageCount);
        return res;
    }

}
