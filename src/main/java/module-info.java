module main.java {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires java.sql;
    requires org.json;
    requires jbcrypt;
    requires MaterialFX;
    requires stripe.java;
    requires jakarta.mail;
    requires jakarta.activation;
    requires jdk.jsobject;  // Pour l'usage de netscape.javascript.JSObject
    requires itextpdf;

    opens Entities to javafx.base, javafx.fxml;
    opens main.java to javafx.fxml;
    exports main.java;
    exports Controllers.monta;
    opens Controllers.monta to javafx.fxml;
    exports Entities;
    exports Services;
}