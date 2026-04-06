package com.tienda.inventariojavafx.controller;

import com.tienda.inventariojavafx.model.Producto;
import com.tienda.inventariojavafx.repository.ProductoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class MainController {

    @FXML private TextField txtBusqueda;
    @FXML private TableView<Producto> tablaProductos;

    private ProductoRepository repository = new ProductoRepository();
    private ObservableList<Producto> listaObservable;
    private FilteredList<Producto> listaFiltrada;

    @FXML
    public void initialize() {

        ((TableColumn<Producto, String>) tablaProductos.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<>("codigo"));
        ((TableColumn<Producto, String>) tablaProductos.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ((TableColumn<Producto, Double>) tablaProductos.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<>("precio"));
        ((TableColumn<Producto, Integer>) tablaProductos.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<>("stock"));
        ((TableColumn<Producto, String>) tablaProductos.getColumns().get(4)).setCellValueFactory(new PropertyValueFactory<>("categoria"));

        listaObservable = FXCollections.observableArrayList(repository.cargarProductos());


        listaFiltrada = new FilteredList<>(listaObservable, p -> true);
        tablaProductos.setItems(listaFiltrada);


        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(producto -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                return producto.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                        producto.getCodigo().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    @FXML
    public void agregarProducto() {
        Producto nuevoProducto = mostrarDialogoProducto(null); // Abre ventanita vacía
        if (nuevoProducto != null) {
            listaObservable.add(nuevoProducto);
            repository.guardarProductos(listaObservable);
            mostrarAlertaconfir("Listo", "Producto agregado exitosamente");
        }
    }


    @FXML
    public void editarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Producto productoEditado = mostrarDialogoProducto(seleccionado);
            if (productoEditado != null) {

                seleccionado.setNombre(productoEditado.getNombre());
                seleccionado.setPrecio(productoEditado.getPrecio());
                seleccionado.setStock(productoEditado.getStock());
                seleccionado.setCategoria(productoEditado.getCategoria());

                tablaProductos.refresh();
                repository.guardarProductos(listaObservable);

            }
        } else {
            mostrarAlerta("Error", "Debes seleccionar un producto de la tabla para editarlo.");
        }
    }


    @FXML
    public void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaObservable.remove(seleccionado);
            repository.guardarProductos(listaObservable);
            mostrarAlerta("Listo", "Producto eliminado exitosamente");
        } else {
            mostrarAlerta("Error", "Debes seleccionar un producto de la tabla para eliminarlo.");
        }
    }

    // --- HERRAMIENTA: VENTANA EMERGENTE PARA CREAR/EDITAR ---
    private Producto mostrarDialogoProducto(Producto p) {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle(p == null ? "Nuevo Producto" : "Editar Producto");
        dialog.setHeaderText("Ingresa los datos del producto");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField txtCodigo = new TextField();
        TextField txtNombre = new TextField();
        TextField txtPrecio = new TextField();
        TextField txtStock = new TextField();
        TextField txtCategoria = new TextField();


        if (p != null) {
            txtCodigo.setText(p.getCodigo());
            txtCodigo.setDisable(true);
            txtNombre.setText(p.getNombre());
            txtPrecio.setText(String.valueOf(p.getPrecio()));
            txtStock.setText(String.valueOf(p.getStock()));
            txtCategoria.setText(p.getCategoria());
        }

        grid.add(new Label("Código:"), 0, 0); grid.add(txtCodigo, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1); grid.add(txtNombre, 1, 1);
        grid.add(new Label("Precio:"), 0, 2); grid.add(txtPrecio, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);  grid.add(txtStock, 1, 3);
        grid.add(new Label("Categoría:"), 0, 4); grid.add(txtCategoria, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    return new Producto(txtCodigo.getText(), txtNombre.getText(),
                            Double.parseDouble(txtPrecio.getText()), Integer.parseInt(txtStock.getText()), txtCategoria.getText());

                } catch (NumberFormatException e) {
                    mostrarAlerta("Error de formato", "Revisa que Precio y Stock sean números válidos.");
                    return null;
                }
            }
            return null;
        });

        Optional<Producto> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }
    private void mostrarAlertaconfir(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }
}
