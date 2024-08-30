package com.openbravo.pos.planSepare;

import java.util.List;

public class PlanSepareDTO {
     private int id;
    private String nombreCliente;
    private List<String> listaProducto;
    private List<String> precioProducto;
    private List<String> listaCantidadProducto;
    private String idCliente;
    private String estadoPagado;
    private double precioAbonado;
    private double precioTotal;
   

    public PlanSepareDTO() {
    }

    public PlanSepareDTO(String nombreCliente, List<String> listaProducto, List<String> precioProducto, String idCliente, double precioAbonado, double precioTotal) {
        this.nombreCliente = nombreCliente;
        this.listaProducto = listaProducto;
        this.precioProducto = precioProducto;
        this.idCliente = idCliente;
        this.precioAbonado = precioAbonado;
        this.precioTotal = precioTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public List<String> getListaProducto() {
        return listaProducto;
    }

    public void setListaProducto(List<String> listaProducto) {
        this.listaProducto = listaProducto;
    }

    public List<String> getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(List<String> precioProducto) {
        this.precioProducto = precioProducto;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public double getPrecioAbonado() {
        return precioAbonado;
    }

    public void setPrecioAbonado(double precioAbonado) {
        this.precioAbonado = precioAbonado;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getEstadoPagado() {
        return estadoPagado;
    }

    public void setEstadoPagado(String estadoPagado) {
        this.estadoPagado = estadoPagado;
    }

    public List<String> getListaCantidadProducto() {
        return listaCantidadProducto;
    }

    public void setListaCantidadProducto(List<String> listaCantidadProducto) {
        this.listaCantidadProducto = listaCantidadProducto;
    }
    
}
