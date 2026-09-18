# Java Trainer

Primera versión mínima funcional de una aplicación educativa JavaFX.

## Requisitos

- JDK 21
- Maven 3.9 o superior

## Ejecutar

```bash
mvn javafx:run
```

La aplicación carga `src/main/resources/ejercicios/ejercicio-1.json`, muestra sus tarjetas y permite arrastrarlas a `CLASE`, `ATRIBUTO` o `MÉTODO`. **Comprobar** calcula el porcentaje mediante una comparación exacta y muestra la explicación de cada respuesta incorrecta.

## Estructura inicial

- `model`: representa el ejercicio, las tarjetas y el resultado.
- `service/JsonService`: carga el ejercicio desde JSON.
- `service/EvaluacionService`: contiene la evaluación determinista.
- `ui/MainView`: construye la pantalla mínima y gestiona el drag & drop.

La aplicación no interpreta enunciados ni utiliza inteligencia artificial. El siguiente paso será agregar relaciones, múltiples ejercicios y el asistente para crear ejercicios.
