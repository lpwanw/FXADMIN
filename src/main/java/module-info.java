module com.csm.admin.fxadmincsm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires com.google.gson;

    opens com.csm.admin.fxadmincsm to javafx.fxml;
    exports com.csm.admin.fxadmincsm;
    exports com.csm.admin.fxadmincsm.controller;
    exports com.csm.model;
    opens com.csm.model to com.google.gson;
    opens com.csm.admin.fxadmincsm.controller to javafx.fxml;
}