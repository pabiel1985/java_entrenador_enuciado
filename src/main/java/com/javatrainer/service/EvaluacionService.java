package com.javatrainer.service;

import com.javatrainer.model.Ejercicio;
import com.javatrainer.model.Resultado;
import com.javatrainer.model.ResultadoTarjeta;
import com.javatrainer.model.Tarjeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluacionService {
    public Resultado evaluar(Ejercicio ejercicio, Map<String, String> respuestas) {
        List<ResultadoTarjeta> detalles = new ArrayList<>();
        int aciertos = 0;

        for (Tarjeta tarjeta : ejercicio.getTarjetas()) {
            ResultadoTarjeta detalle = new ResultadoTarjeta(tarjeta, respuestas.get(tarjeta.getTexto()));
            detalles.add(detalle);
            if (detalle.isCorrecta()) aciertos++;
        }

        int porcentaje = ejercicio.getTarjetas().isEmpty()
                ? 0 : (aciertos * 100) / ejercicio.getTarjetas().size();
        return new Resultado(porcentaje, detalles);
    }
}
