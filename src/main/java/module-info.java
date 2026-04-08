module com.tienda.inventariojavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.tienda.inventariojavafx to javafx.fxml;

    opens com.tienda.inventariojavafx.controller to javafx.fxml;


    opens com.tienda.inventariojavafx.model to javafx.base;

    exports com.tienda.inventariojavafx;
}