package com.javatrainer.ui;

import com.javatrainer.model.Ejercicio;
import com.javatrainer.model.Resultado;
import com.javatrainer.model.ResultadoTarjeta;
import com.javatrainer.model.Tarjeta;
import com.javatrainer.service.EvaluacionService;
import com.javatrainer.service.JsonService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pantalla de práctica con navegación, abandono y tiempo límite por ejercicio. */
public class MainView extends BorderPane {
    private static final int COLUMNA_ANCHO = 240;
    private static final int TIEMPO_POR_EJERCICIO_SEGUNDOS = 15 * 60;
    private static final List<String> RUTAS_EJERCICIOS = List.of(
            "/ejercicios/ejercicio-1.json",
            "/ejercicios/ejercicio-2.json"
    );

    private final Map<String, String> respuestas = new HashMap<>();
    private final VBox resultadoBox = new VBox(6);
    private final VBox contenido = new VBox(18);
    private final Label contadorEjercicio = new Label();
    private final Label temporizador = new Label();
    private Timeline reloj;
    private int ejercicioActual;
    private int segundosRestantes;
    private Ejercicio ejercicio;

    public MainView() {
        cargarEjercicio(0);
        construirInterfaz();
        iniciarTemporizador();
    }

    private void cargarEjercicio(int indice) {
        ejercicioActual = indice;
        respuestas.clear();
        try {
            ejercicio = new JsonService().cargarEjercicio(RUTAS_EJERCICIOS.get(indice));
        } catch (IOException error) {
            ejercicio = new Ejercicio(0, "No se pudo cargar el ejercicio: " + error.getMessage(), List.of());
        }
    }

    private void construirInterfaz() {
        Label titulo = new Label("JAVA TRAINER");
        titulo.getStyleClass().add("titulo");
        setTop(titulo);
        BorderPane.setAlignment(titulo, Pos.CENTER);
        BorderPane.setMargin(titulo, new Insets(18));

        contenido.setPadding(new Insets(20));
        contenido.setFillWidth(true);
        reconstruirContenido();

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setPannable(true);
        setCenter(scroll);
    }

    private void reconstruirContenido() {
        contenido.getChildren().clear();
        actualizarCabecera();
        contenido.getChildren().addAll(
                crearBarraEjercicio(),
                crearEnunciado(),
                crearTarjetasDisponibles(),
                crearZonas(),
                crearControles()
        );
    }

    private void actualizarCabecera() {
        contadorEjercicio.setText("EJERCICIO " + (ejercicioActual + 1) + "/" + RUTAS_EJERCICIOS.size());
        temporizador.getStyleClass().remove("temporizador-alerta");
        temporizador.getStyleClass().add("temporizador");
    }

    private VBox crearBarraEjercicio() {
        Button abandonar = new Button("ABANDONAR EJERCICIO");
        abandonar.getStyleClass().add("boton-secundario");
        abandonar.setOnAction(event -> abandonarEjercicio());

        contadorEjercicio.getStyleClass().add("contador-ejercicio");
        temporizador.setMinWidth(130);
        temporizador.setAlignment(Pos.CENTER_RIGHT);
        HBox fila = new HBox(15, contadorEjercicio, temporizador, abandonar);
        fila.setAlignment(Pos.CENTER_LEFT);
        return new VBox(8, fila);
    }

    private VBox crearEnunciado() {
        Label tituloPanel = new Label("ENUNCIADO");
        tituloPanel.getStyleClass().add("panel-titulo");

        Label texto = new Label(ejercicio.getEnunciado());
        texto.setWrapText(true);
        texto.setMaxWidth(Double.MAX_VALUE);
        texto.setMinWidth(0);
        // No se fija la altura: el Label crece según la cantidad real de texto.
        texto.setMinHeight(Region.USE_PREF_SIZE);
        texto.setPrefHeight(Region.USE_COMPUTED_SIZE);
        texto.getStyleClass().add("enunciado");

        VBox panel = new VBox(6, tituloPanel, texto);
        panel.setFillWidth(true);
        panel.getStyleClass().add("panel-enunciado");
        return panel;
    }

    private VBox crearTarjetasDisponibles() {
        VBox caja = new VBox(8);
        Label titulo = new Label("TARJETAS DISPONIBLES (arrastra cada tarjeta a una categoría)");
        titulo.setWrapText(true);

        FlowPane tarjetas = new FlowPane(10, 10);
        tarjetas.setId("tarjetasDisponibles");
        tarjetas.setPrefWrapLength(1000);
        tarjetas.setOnDragOver(event -> {
            if (event.getGestureSource() instanceof Label) event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });
        tarjetas.setOnDragDropped(event -> {
            if (event.getGestureSource() instanceof Label etiqueta) {
                quitarDePadre(etiqueta);
                tarjetas.getChildren().add(etiqueta);
                respuestas.remove(((Tarjeta) etiqueta.getUserData()).getTexto());
                event.setDropCompleted(true);
            }
            event.consume();
        });
        for (Tarjeta tarjeta : ejercicio.getTarjetas()) tarjetas.getChildren().add(crearTarjeta(tarjeta));
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
        for (String categoria : ejercicio.getCategorias()) {
            FlowPane zona = new FlowPane(8, 8);
            zona.getStyleClass().add("zona");
            zona.setPrefWrapLength(COLUMNA_ANCHO);
            zona.setMinWidth(COLUMNA_ANCHO);
            zona.setPrefWidth(COLUMNA_ANCHO);
            zona.setMaxWidth(COLUMNA_ANCHO);
            zona.setMinHeight(110);
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
        quitarDePadre(tarjeta);
        tarjeta.setMaxWidth(COLUMNA_ANCHO - 24);
        destino.getChildren().add(tarjeta);
        respuestas.put(((Tarjeta) tarjeta.getUserData()).getTexto(), categoria);
    }

    private void quitarDePadre(Label tarjeta) {
        if (tarjeta.getParent() instanceof Pane anterior) anterior.getChildren().remove(tarjeta);
    }

    private VBox crearControles() {
        Button comprobar = new Button("COMPROBAR");
        comprobar.setOnAction(event -> comprobarEjercicio());
        resultadoBox.getStyleClass().add("resultado");
        resultadoBox.setVisible(false);
        resultadoBox.setManaged(false);
        return new VBox(12, comprobar, resultadoBox);
    }

    private void comprobarEjercicio() {
        detenerTemporizador();
        mostrarResultado(new EvaluacionService().evaluar(ejercicio, respuestas));
    }

    private void mostrarResultado(Resultado resultado) {
        resultadoBox.getChildren().clear();
        Label porcentaje = new Label("RESULTADO: " + resultado.porcentaje() + " %");
        porcentaje.getStyleClass().add("porcentaje");
        resultadoBox.getChildren().add(porcentaje);
        for (ResultadoTarjeta detalle : resultado.detalles()) {
            String respuesta = detalle.getRespuestaUsuario() == null ? "sin colocar" : detalle.getRespuestaUsuario();
            Label linea = new Label((detalle.isCorrecta() ? "✓ " : "✗ ") + detalle.getTarjeta().getTexto() + " → " + respuesta);
            linea.setWrapText(true);
            resultadoBox.getChildren().add(linea);
            if (!detalle.isCorrecta()) {
                Label explicacion = new Label("  Esperada: " + detalle.getTarjeta().getTipoCorrecto() + ". " + detalle.getTarjeta().getExplicacion());
                explicacion.setWrapText(true);
                resultadoBox.getChildren().add(explicacion);
            }
        }
        Button siguiente = new Button(ejercicioActual + 1 < RUTAS_EJERCICIOS.size() ? "SIGUIENTE EJERCICIO" : "VOLVER A EMPEZAR");
        siguiente.setOnAction(event -> siguienteEjercicio());
        resultadoBox.getChildren().add(siguiente);
        resultadoBox.setManaged(true);
        resultadoBox.setVisible(true);
    }

    private void siguienteEjercicio() {
        int siguiente = ejercicioActual + 1 < RUTAS_EJERCICIOS.size() ? ejercicioActual + 1 : 0;
        cargarEjercicio(siguiente);
        reconstruirContenido();
        iniciarTemporizador();
    }

    private void abandonarEjercicio() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Querés abandonar el ejercicio actual? Se perderán las respuestas.",
                ButtonType.YES, ButtonType.NO);
        alerta.setTitle("Abandonar ejercicio");
        alerta.setHeaderText("Confirmar abandono");
        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                cargarEjercicio(0);
                reconstruirContenido();
                iniciarTemporizador();
            }
        });
    }

    private void iniciarTemporizador() {
        detenerTemporizador();
        segundosRestantes = TIEMPO_POR_EJERCICIO_SEGUNDOS;
        actualizarTemporizador();
        reloj = new Timeline(new KeyFrame(Duration.seconds(1), event -> avanzarTemporizador()));
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }

    private void avanzarTemporizador() {
        segundosRestantes--;
        actualizarTemporizador();
        if (segundosRestantes <= 0) {
            detenerTemporizador();
            new Alert(Alert.AlertType.INFORMATION, "Se terminó el tiempo. El ejercicio será corregido.").showAndWait();
            comprobarEjercicio();
        }
    }

    private void actualizarTemporizador() {
        int minutos = segundosRestantes / 60;
        int segundos = segundosRestantes % 60;
        temporizador.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));
        if (segundosRestantes <= 60) {
            temporizador.getStyleClass().remove("temporizador");
            temporizador.getStyleClass().add("temporizador-alerta");
        }
    }

    private void detenerTemporizador() {
        if (reloj != null) reloj.stop();
    }
}
