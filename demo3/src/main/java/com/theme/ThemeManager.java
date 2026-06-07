package com.theme;

import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ThemeManager {
    private static ThemeManager instance;
    private Theme currentTheme;
    private ThemeType currentThemeType;

    private ThemeManager() {
        // Default to LIGHT mode
        setThemeType(ThemeType.LIGHT);
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public ThemeType getThemeType() {
        return currentThemeType;
    }

    public void setThemeType(ThemeType type) {
        this.currentThemeType = type;
        if (type == ThemeType.DARK) {
            currentTheme = new DarkTheme();
        } else {
            currentTheme = new LightTheme();
        }
    }

    public boolean isDarkMode() {
        return currentTheme.isDarkMode();
    }

    public void applyTheme(Scene scene) {
        if (scene == null) return;
        try {
            String baseStyle = getClass().getResource("/com/view/css/base-style.css").toExternalForm();
            String darkStyle = getClass().getResource("/com/view/css/dark-mode.css").toExternalForm();

            if (!scene.getStylesheets().contains(baseStyle)) {
                scene.getStylesheets().add(baseStyle);
            }

            if (isDarkMode()) {
                if (!scene.getStylesheets().contains(darkStyle)) {
                    scene.getStylesheets().add(darkStyle);
                }
            } else {
                scene.getStylesheets().remove(darkStyle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyThemeToDialogPane(DialogPane dialogPane) {
        if (dialogPane == null) return;
        try {
            String baseStyle = getClass().getResource("/com/view/css/base-style.css").toExternalForm();
            if (!dialogPane.getStylesheets().contains(baseStyle)) {
                dialogPane.getStylesheets().add(baseStyle);
            }
            if (isDarkMode()) {
                String darkStyle = getClass().getResource("/com/view/css/dark-mode.css").toExternalForm();
                if (!dialogPane.getStylesheets().contains(darkStyle)) {
                    dialogPane.getStylesheets().add(darkStyle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adjustImageForTheme(ImageView imageView) {
        if (imageView == null) return;
        
        Image original = (Image) imageView.getUserData();
        if (original == null) {
            original = imageView.getImage();
            imageView.setUserData(original);
        }
        
        if (original == null) return;

        if (isDarkMode()) {
            imageView.setImage(invertImage(original));
        } else {
            imageView.setImage(original);
        }
    }

    private Image invertImage(Image source) {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();
        if (w <= 0 || h <= 0) {
            return source;
        }
        WritableImage inverted = new WritableImage(w, h);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = inverted.getPixelWriter();
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = reader.getArgb(x, y);
                int a = (argb >> 24) & 0xff;
                int r = 255 - ((argb >> 16) & 0xff);
                int g = 255 - ((argb >> 8) & 0xff);
                int b = 255 - (argb & 0xff);
                int invertedArgb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(x, y, invertedArgb);
            }
        }
        return inverted;
    }
}
