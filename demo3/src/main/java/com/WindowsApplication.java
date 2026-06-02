package com;

import com.controller.mainmenu.MainMenuApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainMenuApplication.class.getResource("/com/view/credit-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Help");
        stage.setScene(scene);
        stage.show();
    }
}
