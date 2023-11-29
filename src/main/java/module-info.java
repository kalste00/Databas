module se.kth.databas.databas {
    requires javafx.controls;
    requires javafx.fxml;


    opens se.kth.databas to javafx.fxml;
    exports se.kth.databas;
}