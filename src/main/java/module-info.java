module com.tienda.inventariojavafx {
    requires javafx.controls;
    requires javafx.fxml;

    // Permite a JavaFX leer tu clase App y tu pantalla FXML
    opens com.tienda.inventariojavafx to javafx.fxml;

    // ¡LA SOLUCIÓN AL ERROR! Permite a JavaFX usar tu Controlador
    opens com.tienda.inventariojavafx.controller to javafx.fxml;

    // Permite a las tablas de JavaFX leer los datos de tu clase Producto (Lo necesitaremos después)
    opens com.tienda.inventariojavafx.model to javafx.base;

    exports com.tienda.inventariojavafx;
}