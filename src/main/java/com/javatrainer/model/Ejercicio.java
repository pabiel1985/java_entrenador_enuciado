package com.javatrainer.model;

import java.util.ArrayList;
import java.util.List;

public class Ejercicio {
    private int id;
    private String enunciado;
    private List<Tarjeta> tarjetas = new ArrayList<>();

    public Ejercicio() { }

    public Ejercicio(int id, String enunciado, List<Tarjeta> tarjetas) {
        this.id = id;
        this.enunciado = enunciado;
        this.tarjetas = tarjetas;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public List<Tarjeta> getTarjetas() { return tarjetas; }
    public void setTarjetas(List<Tarjeta> tarjetas) { this.tarjetas = tarjetas; }
}
