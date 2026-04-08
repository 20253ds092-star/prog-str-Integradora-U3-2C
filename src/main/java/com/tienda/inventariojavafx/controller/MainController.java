package com.tienda.inventariojavafx.controller;

import com.tienda.inventariojavafx.model.Producto;
import com.tienda.inventariojavafx.ProductoService.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class MainController {

    @FXML private TextField txtBusqueda;
    @FXML private TableView<Producto> tablaProductos;

    private ProductoService productoService = new ProductoService();
    private ObservableList<Producto> listaObservable;
    private FilteredList<Producto> listaFiltrada;

    @FXML
    public void initialize() {
        TableColumn<Producto, String> colCodigo = (TableColumn<Producto, String>) tablaProductos.getColumns().get(0);
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Producto, String> colNombre = (TableColumn<Producto, String>) tablaProductos.getColumns().get(1);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, Double> colPrecio = (TableColumn<Producto, Double>) tablaProductos.getColumns().get(2);
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Integer> colStock = (TableColumn<Producto, Integer>) tablaProductos.getColumns().get(3);
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Producto, String> colCategoria = (TableColumn<Producto, String>) tablaProductos.getColumns().get(4);
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        listaObservable = FXCollections.observableArrayList(productoService.obtenerProductos());
        listaFiltrada = new FilteredList<>(listaObservable, p -> true);

        SortedList<Producto> listaOrdenada= new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(listaOrdenada);

        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(producto -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String filtro = newValue.toLowerCase();
                return producto.getNombre().toLowerCase().contains(filtro) ||
                        producto.getCodigo().toLowerCase().contains(filtro) ||
                        producto.getCategoria().toLowerCase().contains(filtro);
            });
        });
    }

    @FXML
    public void agregarProducto() {
        Producto nuevo = mostrarDialogoProducto(null);
        if (nuevo != null) {
            listaObservable.add(nuevo);
            productoService.guardarInventario(listaObservable);
            mostrarAlertaconfir("Listo", "Producto agregado correctamente");
        }
    }

    @FXML
    public void editarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Producto editado = mostrarDialogoProducto(seleccionado);
            if (editado != null) {
                seleccionado.setNombre(editado.getNombre());
                seleccionado.setPrecio(editado.getPrecio());
                seleccionado.setStock(editado.getStock());
                seleccionado.setCategoria(editado.getCategoria());
                tablaProductos.refresh();

                productoService.guardarInventario(listaObservable);
                mostrarAlertaconfir("Listo", "Producto editado correctamente");
            }
        } else {
            mostrarAlerta("Selección requerida", "Selecciona un producto para editar.");
        }
    }

    @FXML
    public void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar Eliminación");
            alerta.setHeaderText("¿Eliminar " + seleccionado.getNombre() + "?");
            alerta.setContentText("Esta acción borrará el producto del inventario permanentemente.");
            Optional<ButtonType> resultado = alerta.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                listaObservable.remove(seleccionado);
                productoService.guardarInventario(listaObservable);
                mostrarAlertaconfir("Listo", "Producto eliminado correctamente");
            }
        } else {
            mostrarAlerta("Selección requerida", "Selecciona el producto que deseas eliminar.");
        }
    }

    private Producto mostrarDialogoProducto(Producto p) {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle(p == null ? "Nuevo Producto" : "Editar Producto");
        ButtonType btnGuardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardarType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(15);
        TextField txtCod = new TextField();
        TextField txtNom = new TextField();
        TextField txtPre = new TextField();
        TextField txtSto = new TextField();
        ComboBox<String> comboCat = new ComboBox<>();
        comboCat.getItems().addAll("Frituras", "Lácteos", "Bebidas", "Panadería", "Limpieza");
        comboCat.setMaxWidth(Double.MAX_VALUE);

        if (p != null) {
            txtCod.setText(p.getCodigo());
            txtCod.setDisable(true);
            txtNom.setText(p.getNombre());
            txtPre.setText(String.valueOf(p.getPrecio()));
            txtSto.setText(String.valueOf(p.getStock()));
            comboCat.setValue(p.getCategoria());
        }

        grid.add(new Label("Código:"), 0, 0); grid.add(txtCod, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1); grid.add(txtNom, 1, 1);
        grid.add(new Label("Precio:"), 0, 2); grid.add(txtPre, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);  grid.add(txtSto, 1, 3);
        grid.add(new Label("Categoría:"), 0, 4); grid.add(comboCat, 1, 4);
        dialog.getDialogPane().setContent(grid);

        final Button btGuardar = (Button) dialog.getDialogPane().lookupButton(btnGuardarType);

        btGuardar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                String cod = txtCod.getText().trim();
                String nom = txtNom.getText().trim();
                String cat = comboCat.getValue();

                double pre = Double.parseDouble(txtPre.getText().trim());
                int sto = Integer.parseInt(txtSto.getText().trim());

                Producto productoTemporal = new Producto(cod, nom, pre, sto, cat);

                boolean esNuevo = (p == null);
                productoService.validarProducto(productoTemporal, esNuevo, listaObservable);

            } catch (NumberFormatException e) {
                event.consume();
                mostrarAlerta("Formato incorrecto", "El precio y el stock deben ser números válidos. No dejes estos campos vacíos.");
            } catch (IllegalArgumentException e) {
                event.consume();
                mostrarAlerta("Datos inválidos", e.getMessage());
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardarType) {
                return new Producto(txtCod.getText().trim(), txtNom.getText().trim(),
                        Double.parseDouble(txtPre.getText().trim()), Integer.parseInt(txtSto.getText().trim()), comboCat.getValue());
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
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