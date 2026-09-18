# Java Trainer

Aplicación educativa de escritorio para practicar cómo transformar un enunciado en clases, atributos, métodos, constructores y objetos.

## Ejecutar desde IntelliJ IDEA

1. Instalar JDK 21.
2. Abrir la carpeta del proyecto desde **File > Open**.
3. Seleccionar el archivo `pom.xml` y elegir **Open as Project** si IntelliJ lo solicita.
4. Esperar a que Maven descargue las dependencias.
5. Confirmar que el SDK del proyecto sea JDK 21 en **File > Project Structure**.
6. Ejecutar la clase `com.javatrainer.Main` con el botón verde de IntelliJ.

También se puede ejecutar desde la ventana Maven con `Plugins > javafx > javafx:run`.

## Ejercicio implementado

El primer ejercicio es **Ejercicio 1/10 · Biblioteca** y se carga desde:

`src/main/resources/ejercicios/ejercicio-1.json`

Actualmente se pueden arrastrar tarjetas a las categorías necesarias para resolverlo:

- CLASE
- ATRIBUTO
- MÉTODO
- CONSTRUCTOR
- OBJETO

La interfaz también deja preparadas las categorías futuras: CLASE PADRE, CLASE HIJA, INTERFACE, CONSTANTE, VARIABLE, PRIVATE, PUBLIC, PROTECTED, EXTENDS e IMPLEMENTS.

Al pulsar **COMPROBAR**, la aplicación compara exactamente cada respuesta con la solución almacenada en JSON, calcula el porcentaje y muestra los errores con su explicación. No utiliza inteligencia artificial ni interpreta automáticamente el enunciado.
