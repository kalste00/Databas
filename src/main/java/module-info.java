module se.kth.databas.databas {
    requires java.sql;

    requires javafx.controls;
    requires javafx.base;

    opens se.kth.databas to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports se.kth.databas;
}