module com.example.financialtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;


    opens com.example.financialtracker to javafx.fxml;
    exports com.example.financialtracker;
}