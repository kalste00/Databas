module se.kth.databas.databas{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens se.kth.databas to javafx.fxml;
    opens se.kth.databas.model to javafx.base;
    exports se.kth.databas;
}