package com.view.vis.pseudocode;

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

        // Custom CellFactory to change text color based on active index
        this.listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    setText(item);
                    if (getIndex() == activeIndex) {
                        setTextFill(Color.web("#e74c3c")); // Red for active step
                        setStyle("-fx-font-weight: bold; -fx-font-family: monospace; -fx-font-size: 12px;");
                    } else {
                        setTextFill(Color.web("#555555")); // Gray for past steps
                        setStyle("-fx-font-weight: normal; -fx-font-family: monospace; -fx-font-size: 12px;");
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
