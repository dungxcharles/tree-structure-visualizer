package com.view.vis.pseudocode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.application.Platform;

public class ListViewPseudoCodeDisplay implements PseudoCodeDisplay {

    private final ListView<String> listView;
    private final ObservableList<String> items;
    private int activeIndex = -1;

    public ListViewPseudoCodeDisplay(ListView<String> listView) {
        this.listView = listView;
        this.items = FXCollections.observableArrayList();
        this.listView.setItems(this.items);

        // Custom CellFactory to manage style classes instead of inline styles
        this.listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().remove("pseudo-step-cell");
                    getStyleClass().remove("active-step");
                } else {
                    setText(item);
                    if (!getStyleClass().contains("pseudo-step-cell")) {
                        getStyleClass().add("pseudo-step-cell");
                    }
                    if (getIndex() == activeIndex) {
                        if (!getStyleClass().contains("active-step")) {
                            getStyleClass().add("active-step");
                        }
                    } else {
                        getStyleClass().remove("active-step");
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
