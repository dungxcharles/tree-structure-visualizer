module com {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.model.tree to javafx.fxml;
    exports com.model.tree;
    exports com.model.node;
    opens com.view to javafx.fxml;
    exports com.view;
    exports com.controller;
    opens com.controller to javafx.fxml;
    exports com.controller.mainmenu;
    opens com.controller.mainmenu to javafx.fxml;
    exports com.controller.workspace;
    opens com.controller.workspace to javafx.fxml;
    opens com.controller.treeselection to javafx.fxml;
}