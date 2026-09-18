package com.javatrainer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatrainer.model.Ejercicio;

import java.io.IOException;
import java.io.InputStream;

public class JsonService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Ejercicio cargarEjercicio(String ruta) throws IOException {
        try (InputStream recurso = getClass().getResourceAsStream(ruta)) {
            if (recurso == null) {
                throw new IOException("No se encontró el recurso: " + ruta);
            }
            return objectMapper.readValue(recurso, Ejercicio.class);
        }
    }
}
