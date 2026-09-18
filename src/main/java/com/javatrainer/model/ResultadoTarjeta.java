package com.javatrainer.model;

public class ResultadoTarjeta {
    private final Tarjeta tarjeta;
    private final String respuestaUsuario;
    private final boolean correcta;

    public ResultadoTarjeta(Tarjeta tarjeta, String respuestaUsuario) {
        this.tarjeta = tarjeta;
        this.respuestaUsuario = respuestaUsuario;
        this.correcta = tarjeta.getTipoCorrecto().equals(respuestaUsuario);
    }

    public Tarjeta getTarjeta() { return tarjeta; }
    public String getRespuestaUsuario() { return respuestaUsuario; }
    public boolean isCorrecta() { return correcta; }
}
