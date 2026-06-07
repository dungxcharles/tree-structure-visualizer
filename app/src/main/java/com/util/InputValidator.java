package com.util;

import javafx.scene.control.TextField;

public class InputValidator {
    public static int getValidInt(TextField textField) throws NullPointerException, NumberFormatException {
        if (textField == null)
            throw new NullPointerException("TextField is null");

        String valueStr = textField.getText();
        if (valueStr == null || valueStr.trim().isEmpty())
            throw new NumberFormatException("TextField is empty");

        return Integer.parseInt(valueStr.trim());
    }
}
