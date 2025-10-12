module com.mycompany.coit20258assignment2 {
    requires javafx.controls;
    requires javafx.fxml;

    // Controllers need reflective access for @FXML injection
    opens com.mycompany.coit20258assignment2 to javafx.fxml;

    exports com.mycompany.coit20258assignment2;
}
