package com.javatrainer.ui;

import com.javatrainer.model.Ejercicio;
import com.javatrainer.model.Resultado;
import com.javatrainer.model.ResultadoTarjeta;
import com.javatrainer.model.Tarjeta;
import com.javatrainer.service.EvaluacionService;
import com.javatrainer.service.JsonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainView extends BorderPane {
    private static final String[] CATEGORIAS = {"CLASE", "ATRIBUTO", "MÉTODO"};
    private final Map<String, String> respuestas = new HashMap<>();
    private final Map<String, FlowPane> zonas = new HashMap<>();
    private final VBox resultadoBox = new VBox(6);
    private Ejercicio ejercicio;

    public MainView() {
        cargarEjercicio();
        construirInterfaz();
    }

    private void cargarEjercicio() {
        try {
            ejercicio = new JsonService().cargarEjercicio("/ejercicios/ejercicio-1.json");
        } catch (IOException error) {
            ejercicio = new Ejercicio(0, "No se pudo cargar el ejercicio: " + error.getMessage(), java.util.List.of());
        }
    }

    private void construirInterfaz() {
        Label titulo = new Label("JAVA TRAINER");
        titulo.getStyleClass().add("titulo");
        setTop(titulo);
        BorderPane.setAlignment(titulo, Pos.CENTER);
        BorderPane.setMargin(titulo, new Insets(18));

        VBox contenido = new VBox(18);
        contenido.setPadding(new Insets(20));
        contenido.getChildren().addAll(crearEnunciado(), crearTarjetasDisponibles(), crearZonas(), crearControles());
        setCenter(new ScrollPane(contenido));
    }

    private TitledPane crearEnunciado() {
        Label texto = new Label(ejercicio.getEnunciado());
        texto.setWrapText(true);
        texto.getStyleClass().add("enunciado");
        return new TitledPane("ENUNCIADO", texto);
    }

    private VBox crearTarjetasDisponibles() {
        VBox caja = new VBox(8);
        Label titulo = new Label("TARJETAS DISPONIBLES (arrastra cada tarjeta a una categoría)");
        FlowPane tarjetas = new FlowPane(10, 10);
        tarjetas.setId("tarjetasDisponibles");
        for (Tarjeta tarjeta : ejercicio.getTarjetas()) tarjetas.getChildren().add(crearTarjeta(tarjeta));
        caja.getChildren().addAll(titulo, tarjetas);
        return caja;
    }

    private Label crearTarjeta(Tarjeta tarjeta) {
        Label etiqueta = new Label(tarjeta.getTexto());
        etiqueta.getStyleClass().add("tarjeta");
        etiqueta.setUserData(tarjeta);
        etiqueta.setOnDragDetected(event -> {
            Dragboard dragboard = etiqueta.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent contenido = new ClipboardContent();
            contenido.putString(tarjeta.getTexto());
            dragboard.setContent(contenido);
            event.consume();
        });
        return etiqueta;
    }

    private HBox crearZonas() {
        HBox contenedor = new HBox(15);
        for (String categoria : CATEGORIAS) {
            FlowPane zona = new FlowPane(8, 8);
            zona.getStyleClass().add("zona");
            zona.setPrefHeight(110);
            zona.setOnDragOver(event -> {
                if (event.getGestureSource() instanceof Label) event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            });
            zona.setOnDragDropped(event -> {
                if (event.getGestureSource() instanceof Label etiqueta) {
                    moverTarjeta(etiqueta, zona, categoria);
                    event.setDropCompleted(true);
                }
                event.consume();
            });
            VBox columna = new VBox(6, new Label(categoria), zona);
            columna.getStyleClass().add("columna");
            HBox.setHgrow(columna, Priority.ALWAYS);
            zonas.put(categoria, zona);
            contenedor.getChildren().add(columna);
        }
        return contenedor;
    }

    private void moverTarjeta(Label tarjeta, FlowPane destino, String categoria) {
        if (tarjeta.getParent() instanceof Pane anterior) anterior.getChildren().remove(tarjeta);
        destino.getChildren().add(tarjeta);
        respuestas.put(((Tarjeta) tarjeta.getUserData()).getTexto(), categoria);
    }

    private VBox crearControles() {
        Button comprobar = new Button("COMPROBAR");
        comprobar.setOnAction(event -> mostrarResultado(new EvaluacionService().evaluar(ejercicio, respuestas)));
        resultadoBox.getStyleClass().add("resultado");
        resultadoBox.setVisible(false);
        return new VBox(12, comprobar, resultadoBox);
    }

    private void mostrarResultado(Resultado resultado) {
        resultadoBox.getChildren().clear();
        Label porcentaje = new Label("RESULTADO: " + resultado.porcentaje() + " %");
        porcentaje.getStyleClass().add("porcentaje");
        resultadoBox.getChildren().add(porcentaje);
        for (ResultadoTarjeta detalle : resultado.detalles()) {
            String respuesta = detalle.getRespuestaUsuario() == null ? "sin colocar" : detalle.getRespuestaUsuario();
            Label linea = new Label((detalle.isCorrecta() ? "✓ " : "✗ ") + detalle.getTarjeta().getTexto()
                    + " → " + respuesta);
            resultadoBox.getChildren().add(linea);
            if (!detalle.isCorrecta()) {
                resultadoBox.getChildren().add(new Label("  Esperada: " + detalle.getTarjeta().getTipoCorrecto()
                        + ". " + detalle.getTarjeta().getExplicacion()));
            }
        }
        resultadoBox.setVisible(true);
    }
}
