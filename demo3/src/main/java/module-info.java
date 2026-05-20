module com.demo3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.demo3 to javafx.fxml;
    exports com.demo3.model.TreeNode;
    opens com.view to javafx.fxml;
    exports com.view;
    exports com.controller;
    opens com.controller to javafx.fxml;
    exports com.controller.mainmenu;
    opens com.controller.mainmenu to javafx.fxml;
}