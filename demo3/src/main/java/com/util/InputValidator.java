package com.util;

import javafx.scene.control.TextField;

public class InputValidator {

    /**
     * Extracts and validates an integer from a TextField.
     *
     * @param textField The TextField to extract the integer from.
     * @return The parsed integer value.
     * @throws NullPointerException  If the TextField is null.
     * @throws NumberFormatException If the TextField is empty or does not contain a valid integer.
     */
    public static int getValidInt(TextField textField) throws NullPointerException, NumberFormatException {
        if (textField == null)
            throw new NullPointerException("TextField is null");

        String valueStr = textField.getText();
        if (valueStr == null || valueStr.trim().isEmpty())
            throw new NumberFormatException("TextField is empty");

        return Integer.parseInt(valueStr.trim());
    }
}
