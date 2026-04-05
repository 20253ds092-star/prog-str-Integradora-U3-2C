package com.tienda.inventariojavafx.model;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private int stock;
    private String categoria;

    // Constructor que recibe los 5 datos
    public Producto(String codigo, String nombre, double precio, int stock, String categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // GETTERS: Súper importantes para que la tabla de JavaFX pueda mostrar el texto
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getCategoria() { return categoria; }

    // SETTERS
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // Método para guardar la línea en el archivo de texto separada por "pipes" (|)
    @Override
    public String toString() {
        return codigo + "|" + nombre + "|" + precio + "|" + stock + "|" + categoria;
    }
}