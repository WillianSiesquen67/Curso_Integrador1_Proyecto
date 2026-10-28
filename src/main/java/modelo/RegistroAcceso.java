package modelo;

import java.time.LocalDateTime;

public class RegistroAcceso {
    private int id;
    private String persona;
    private String tipoMovimiento;
    private String resultado;
    private String detalle;
    private LocalDateTime fechaHora;

    public RegistroAcceso() {}
    public RegistroAcceso(String persona, String tipoMovimiento, String resultado, String detalle, LocalDateTime fechaHora) {
        this.persona = persona; this.tipoMovimiento = tipoMovimiento; this.resultado = resultado;
        this.detalle = detalle; this.fechaHora = fechaHora;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPersona() { return persona; }
    public void setPersona(String persona) { this.persona = persona; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
