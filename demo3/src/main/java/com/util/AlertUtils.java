package com.util;

import javafx.scene.control.Alert;

public class AlertUtils {

    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        com.theme.ThemeManager.getInstance().applyThemeToDialogPane(alert.getDialogPane());
        alert.showAndWait();
    }
}
