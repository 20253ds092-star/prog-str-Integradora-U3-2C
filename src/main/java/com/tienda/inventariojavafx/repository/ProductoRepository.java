package com.tienda.inventariojavafx.repository;

import com.tienda.inventariojavafx.model.Producto;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {


    private final String ARCHIVO = "productos.txt";


    public List<Producto> cargarProductos() {
        List<Producto> lista = new ArrayList<>();
        File file = new File(ARCHIVO);

        if (!file.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 5) {
                    Producto p = new Producto(
                            datos[0],
                            datos[1],
                            Double.parseDouble(datos[2]),
                            Integer.parseInt(datos[3]),
                            datos[4]
                    );
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            System.out.println("Hubo un error al cargar los productos: " + e.getMessage());
        }
        return lista;
    }


    public void guardarProductos(ObservableList<Producto> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Producto p : productos) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Hubo un error al guardar los productos: " + e.getMessage());
        }
    }
}