package com.javatrainer.model;

public class Tarjeta {
    private String texto;
    private String tipoCorrecto;
    private String explicacion;

    public Tarjeta() { }

    public Tarjeta(String texto, String tipoCorrecto, String explicacion) {
        this.texto = texto;
        this.tipoCorrecto = tipoCorrecto;
        this.explicacion = explicacion;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getTipoCorrecto() { return tipoCorrecto; }
    public void setTipoCorrecto(String tipoCorrecto) { this.tipoCorrecto = tipoCorrecto; }
    public String getExplicacion() { return explicacion; }
    public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
}
