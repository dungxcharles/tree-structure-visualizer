module com {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    exports com;

    opens com to javafx.graphics;

    opens com.model.tree to javafx.fxml;

    exports com.model.tree;
    exports com.model.node;

    exports com.controller;

    opens com.controller to javafx.fxml;

    exports com.controller.mainmenu;

    opens com.controller.mainmenu to javafx.fxml;

    exports com.controller.workspace;

    opens com.controller.workspace to javafx.fxml;
    opens com.controller.treeselection to javafx.fxml;

    exports com.controller.settings;

    opens com.controller.settings to javafx.fxml;

    exports com.controller.credits;

    opens com.controller.credits to javafx.fxml;
}