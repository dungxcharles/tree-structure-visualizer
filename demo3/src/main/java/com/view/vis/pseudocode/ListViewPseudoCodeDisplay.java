package com.view.vis.pseudocode;

import com.controller.NavigationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.application.Platform;

public class ListViewPseudoCodeDisplay implements PseudoCodeDisplay {

    private final ListView<String> listView;
    private final ObservableList<String> items;
    private int activeIndex = -1;

    public ListViewPseudoCodeDisplay(ListView<String> listView) {
        this.listView = listView;
        this.items = FXCollections.observableArrayList();
        this.listView.setItems(this.items);

        // Custom CellFactory to change text color based on active index and active theme
        this.listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    boolean isDark = NavigationManager.getInstance().isDarkMode();
                    if (getIndex() == activeIndex) {
                        if (isDark) {
                            setTextFill(Color.web("#ffd54f")); // Gold/amber text in dark mode
                            setStyle("-fx-background-color: #5e4a00; -fx-font-weight: bold; -fx-font-family: monospace; -fx-font-size: 12px;"); // Dark gold highlight
                        } else {
                            setTextFill(Color.BLACK); // Black text for readability on yellow
                            setStyle("-fx-background-color: #ffeb3b; -fx-font-weight: bold; -fx-font-family: monospace; -fx-font-size: 12px;"); // Bright yellow
                        }
                    } else {
                        if (isDark) {
                            setTextFill(Color.web("#dddddd")); // Light grey in dark mode
                        } else {
                            setTextFill(Color.web("#555555")); // Gray for past steps in light mode
                        }
                        setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-font-family: monospace; -fx-font-size: 12px;");
                    }
                }
            }
        });
    }

    @Override
    public void clear() {
        Platform.runLater(() -> {
            items.clear();
            activeIndex = -1;
        });
    }

    @Override
    public void addAndHighlightStep(String message) {
        Platform.runLater(() -> {
            items.add(message);
            activeIndex = items.size() - 1;
            listView.refresh();
            listView.scrollTo(activeIndex);
        });
    }
}
