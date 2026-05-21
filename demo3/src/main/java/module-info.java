module com {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.model.TreeNode to javafx.fxml;
    exports com.model.TreeNode;
    opens com.view to javafx.fxml;
    exports com.view;
    exports com.controller;
    opens com.controller to javafx.fxml;
    exports com.controller.mainmenu;
    opens com.controller.mainmenu to javafx.fxml;
}