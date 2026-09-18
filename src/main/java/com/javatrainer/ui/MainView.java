package com.javatrainer.ui;

import com.javatrainer.model.Ejercicio;
import com.javatrainer.model.Resultado;
import com.javatrainer.model.ResultadoTarjeta;
import com.javatrainer.model.Tarjeta;
import com.javatrainer.service.EvaluacionService;
import com.javatrainer.service.JsonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Pantalla de práctica del primer ejercicio. */
public class MainView extends BorderPane {
    private static final int COLUMNA_ANCHO = 240;
    private static final String[] CATEGORIAS = {
            "CLASE", "CLASE PADRE", "CLASE HIJA", "INTERFACE",
            "ATRIBUTO", "CONSTANTE", "VARIABLE", "MÉTODO",
            "CONSTRUCTOR", "OBJETO", "PRIVATE", "PUBLIC",
            "PROTECTED", "EXTENDS", "IMPLEMENTS"
    };

    private final Map<String, String> respuestas = new HashMap<>();
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
        contenido.getChildren().addAll(
                crearEnunciado(),
                crearTarjetasDisponibles(),
                crearZonas(),
                crearControles()
        );

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        setCenter(scroll);
    }

    private TitledPane crearEnunciado() {
        Label texto = new Label(ejercicio.getEnunciado());
        texto.setWrapText(true);
        texto.setMaxWidth(Double.MAX_VALUE);
        texto.getStyleClass().add("enunciado");
        return new TitledPane("ENUNCIADO", texto);
    }

    private VBox crearTarjetasDisponibles() {
        VBox caja = new VBox(8);
        Label titulo = new Label("TARJETAS DISPONIBLES (arrastra cada tarjeta a una categoría)");
        titulo.setWrapText(true);

        FlowPane tarjetas = new FlowPane(10, 10);
        tarjetas.setId("tarjetasDisponibles");
        tarjetas.setPrefWrapLength(1000);
        tarjetas.setHgap(10);
        tarjetas.setVgap(10);
        tarjetas.setOnDragOver(event -> {
            if (event.getGestureSource() instanceof Label) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        tarjetas.setOnDragDropped(event -> {
            if (event.getGestureSource() instanceof Label etiqueta) {
                if (etiqueta.getParent() instanceof Pane anterior) {
                    anterior.getChildren().remove(etiqueta);
                }
                tarjetas.getChildren().add(etiqueta);
                respuestas.remove(((Tarjeta) etiqueta.getUserData()).getTexto());
                event.setDropCompleted(true);
            }
            event.consume();
        });

        for (Tarjeta tarjeta : ejercicio.getTarjetas()) {
            tarjetas.getChildren().add(crearTarjeta(tarjeta));
        }
        caja.getChildren().addAll(titulo, tarjetas);
        return caja;
    }

    private Label crearTarjeta(Tarjeta tarjeta) {
        Label etiqueta = new Label(tarjeta.getTexto());
        etiqueta.getStyleClass().add("tarjeta");
        etiqueta.setWrapText(true);
        etiqueta.setMaxWidth(COLUMNA_ANCHO - 20);
        etiqueta.setMinHeight(Region.USE_PREF_SIZE);
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

    private FlowPane crearZonas() {
        FlowPane contenedor = new FlowPane(12, 12);
        contenedor.setPrefWrapLength(1000);

        for (String categoria : CATEGORIAS) {
            FlowPane zona = new FlowPane(8, 8);
            zona.getStyleClass().add("zona");
            zona.setPrefWrapLength(COLUMNA_ANCHO);
            zona.setMinWidth(COLUMNA_ANCHO);
            zona.setPrefWidth(COLUMNA_ANCHO);
            zona.setMaxWidth(COLUMNA_ANCHO);
            zona.setMinHeight(110);
            zona.setPrefHeight(110);
            zona.setOnDragOver(event -> {
                if (event.getGestureSource() instanceof Label) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });
            zona.setOnDragDropped(event -> {
                if (event.getGestureSource() instanceof Label etiqueta) {
                    moverTarjeta(etiqueta, zona, categoria);
                    event.setDropCompleted(true);
                }
                event.consume();
            });

            Label nombre = new Label(categoria);
            nombre.getStyleClass().add("categoria");
            VBox columna = new VBox(6, nombre, zona);
            columna.getStyleClass().add("columna");
            columna.setPrefWidth(COLUMNA_ANCHO);
            columna.setMinWidth(COLUMNA_ANCHO);
            columna.setMaxWidth(COLUMNA_ANCHO);
            contenedor.getChildren().add(columna);
        }
        return contenedor;
    }

    private void moverTarjeta(Label tarjeta, FlowPane destino, String categoria) {
        if (tarjeta.getParent() instanceof Pane anterior) {
            anterior.getChildren().remove(tarjeta);
        }
        tarjeta.setMaxWidth(COLUMNA_ANCHO - 24);
        destino.getChildren().add(tarjeta);
        respuestas.put(((Tarjeta) tarjeta.getUserData()).getTexto(), categoria);
    }

    private VBox crearControles() {
        Button comprobar = new Button("COMPROBAR");
        comprobar.setOnAction(event -> {
            Resultado resultado = new EvaluacionService().evaluar(ejercicio, respuestas);
            mostrarResultado(resultado);
        });

        resultadoBox.getStyleClass().add("resultado");
        resultadoBox.setVisible(false);
        resultadoBox.setManaged(false);
        return new VBox(12, comprobar, resultadoBox);
    }

    private void mostrarResultado(Resultado resultado) {
        resultadoBox.getChildren().clear();
        Label porcentaje = new Label("RESULTADO: " + resultado.porcentaje() + " %");
        porcentaje.getStyleClass().add("porcentaje");
        resultadoBox.getChildren().add(porcentaje);

        for (ResultadoTarjeta detalle : resultado.detalles()) {
            String respuesta = detalle.getRespuestaUsuario() == null
                    ? "sin colocar"
                    : detalle.getRespuestaUsuario();
            Label linea = new Label((detalle.isCorrecta() ? "✓ " : "✗ ")
                    + detalle.getTarjeta().getTexto() + " → " + respuesta);
            linea.setWrapText(true);
            resultadoBox.getChildren().add(linea);

            if (!detalle.isCorrecta()) {
                Label explicacion = new Label("  Esperada: "
                        + detalle.getTarjeta().getTipoCorrecto() + ". "
                        + detalle.getTarjeta().getExplicacion());
                explicacion.setWrapText(true);
                resultadoBox.getChildren().add(explicacion);
            }
        }

        resultadoBox.setManaged(true);
        resultadoBox.setVisible(true);
    }
}
