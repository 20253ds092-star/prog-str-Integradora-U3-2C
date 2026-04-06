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
        // 1. Configurar columnas (Asegúrate que los nombres coincidan con los atributos de tu clase Producto)
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

        // 2. Cargar datos del repositorio
        listaObservable = FXCollections.observableArrayList(repository.cargarProductos());

        // 3. Configurar la lista filtrada
        listaFiltrada = new FilteredList<>(listaObservable, p -> true);

        // Importante: La tabla debe mostrar la lista filtrada
        tablaProductos.setItems(listaFiltrada);

        // 4. Lógica del buscador en tiempo real
        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(producto -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String filtro = newValue.toLowerCase();
                return producto.getNombre().toLowerCase().contains(filtro) ||
                        producto.getCodigo().toLowerCase().contains(filtro);
            });
        });
    }

    @FXML
    public void agregarProducto() {
        Producto nuevo = mostrarDialogoProducto(null);
        if (nuevo != null) {
            // Validar que el código no esté duplicado (Requisito 4.C)
            boolean duplicado = listaObservable.stream().anyMatch(p -> p.getCodigo().equals(nuevo.getCodigo()));
            if (duplicado) {
                mostrarAlerta("Código duplicado", "Ya existe un producto con el código: " + nuevo.getCodigo());
                return;
            }
            listaObservable.add(nuevo);
            repository.guardarProductos(listaObservable);
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
                repository.guardarProductos(listaObservable);
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
                repository.guardarProductos(listaObservable);
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
        grid.setHgap(10); grid.setVgap(10);

        TextField txtCod = new TextField();
        TextField txtNom = new TextField();
        TextField txtPre = new TextField();
        TextField txtSto = new TextField();
        TextField txtCat = new TextField();

        if (p != null) {
            txtCod.setText(p.getCodigo());
            txtCod.setDisable(true);
            txtNom.setText(p.getNombre());
            txtPre.setText(String.valueOf(p.getPrecio()));
            txtSto.setText(String.valueOf(p.getStock()));
            txtCat.setText(p.getCategoria());
        }

        grid.add(new Label("Código:"), 0, 0); grid.add(txtCod, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1); grid.add(txtNom, 1, 1);
        grid.add(new Label("Precio:"), 0, 2); grid.add(txtPre, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);  grid.add(txtSto, 1, 3);
        grid.add(new Label("Categoría:"), 0, 4); grid.add(txtCat, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardarType) {
                try {
                    // Validaciones mínimas (Requisito 4.C)
                    if(txtNom.getText().length() < 3) throw new Exception("Nombre muy corto");
                    double precio = Double.parseDouble(txtPre.getText());
                    int stock = Integer.parseInt(txtSto.getText());
                    if(precio <= 0 || stock < 0) throw new Exception("Valores lógicos");

                    return new Producto(txtCod.getText(), txtNom.getText(), precio, stock, txtCat.getText());
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error de formato", "Precio y Stock deben ser números.");
                } catch (Exception e) {
                    mostrarAlerta("Validación", e.getMessage());
                }
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
}