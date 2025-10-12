module com.mycompany.coit20258assignment2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // Controllers need reflective access for @FXML injection
    opens com.mycompany.coit20258assignment2 to javafx.fxml;

    exports com.mycompany.coit20258assignment2;
    exports com.mycompany.coit20258assignment2.common;
    exports com.mycompany.coit20258assignment2.server;
    exports com.mycompany.coit20258assignment2.server.dao;
}
