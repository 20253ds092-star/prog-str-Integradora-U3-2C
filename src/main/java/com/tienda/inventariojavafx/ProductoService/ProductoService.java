
package com.tienda.inventariojavafx.ProductoService;

import com.tienda.inventariojavafx.model.Producto;
import com.tienda.inventariojavafx.repository.ProductoRepository;
import javafx.collections.ObservableList;

import java.util.List;

public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService() {
        this.repository = new ProductoRepository();
    }

    public List<Producto> obtenerProductos() {
        return repository.cargarProductos();
    }

    public void guardarInventario(List<Producto> productos) {
        repository.guardarProductos((ObservableList<Producto>) productos);
    }

    public void validarProducto(Producto producto, boolean esNuevo, List<Producto> inventarioActual) {


        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty() ||
                producto.getNombre() == null || producto.getNombre().trim().isEmpty() ||
                producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }


        if (producto.getNombre().trim().length() < 3) {
            throw new IllegalArgumentException("El nombre del producto debe tener al menos 3 caracteres.");
        }


        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }

        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }


        if (esNuevo) {
            boolean codigoDuplicado = inventarioActual.stream()
                    .anyMatch(p -> p.getCodigo().equalsIgnoreCase(producto.getCodigo().trim()));
            if (codigoDuplicado) {
                throw new IllegalArgumentException("El código '" + producto.getCodigo() + "' ya existe en el inventario.");
            }
        }
    }
}