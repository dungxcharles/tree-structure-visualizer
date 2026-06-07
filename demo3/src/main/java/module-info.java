module com.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    exports com;
    exports com.view;

    exports com.model.tree;
    exports com.model.node;
    exports com.model.exception;

    exports com.controller;
    exports com.controller.mainmenu;
    exports com.controller.workspace;
    exports com.controller.settings;
    exports com.controller.credits;

    opens com to javafx.graphics;
    opens com.view to javafx.fxml;

    opens com.model.tree to javafx.fxml;

    opens com.controller to javafx.fxml;
    opens com.controller.mainmenu to javafx.fxml;
    opens com.controller.workspace to javafx.fxml;
    opens com.controller.treeselection to javafx.fxml;
    opens com.controller.settings to javafx.fxml;
    opens com.controller.credits to javafx.fxml;
}
