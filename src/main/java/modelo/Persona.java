package modelo;

public class Persona {
    private int id;
    private String nombres;
    private String tipo;
    private String empresa;
    private String identificadorBiometrico;
    private boolean activa;

    public Persona() {}
    public Persona(String nombres, String tipo, String empresa, String identificadorBiometrico) {
        this.nombres = nombres;
        this.tipo = tipo;
        this.empresa = empresa;
        this.identificadorBiometrico = identificadorBiometrico;
        this.activa = true;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getIdentificadorBiometrico() { return identificadorBiometrico; }
    public void setIdentificadorBiometrico(String identificadorBiometrico) { this.identificadorBiometrico = identificadorBiometrico; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
