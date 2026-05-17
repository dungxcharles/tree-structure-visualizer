package com.view;
import com.demo3.HelloApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowsApplication extends Application{
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/view/workspace.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Help");
        stage.setScene(scene);
        stage.show();
    }
}
