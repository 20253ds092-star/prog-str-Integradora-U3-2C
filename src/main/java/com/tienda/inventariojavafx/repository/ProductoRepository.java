package com.tienda.inventariojavafx.repository;

import com.tienda.inventariojavafx.model.Producto;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {

    // Nombre del archivo donde se guardará todo
    private final String ARCHIVO = "productos.txt";

    // Método para LEER los datos del archivo txt
    public List<Producto> cargarProductos() {
        List<Producto> lista = new ArrayList<>();
        File file = new File(ARCHIVO);

        if (!file.exists()) {
            return lista; // Si el archivo no existe aún, regresamos la lista vacía
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|"); // Separamos el texto por el símbolo "|"
                if (datos.length == 5) {
                    Producto p = new Producto(
                            datos[0],                     // Código
                            datos[1],                     // Nombre
                            Double.parseDouble(datos[2]), // Precio
                            Integer.parseInt(datos[3]),   // Stock
                            datos[4]                      // Categoría
                    );
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            System.out.println("Hubo un error al cargar los productos: " + e.getMessage());
        }
        return lista;
    }

    // Método para GUARDAR los datos en el archivo txt
    public void guardarProductos(ObservableList<Producto> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Producto p : productos) {
                bw.write(p.toString()); // Usa el toString() de Producto que ya configuramos
                bw.newLine();           // Salto de línea para el siguiente producto
            }
        } catch (Exception e) {
            System.out.println("Hubo un error al guardar los productos: " + e.getMessage());
        }
    }
}