module com.example.parser {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.parser to javafx.fxml;
    exports com.example.parser;
}