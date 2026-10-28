package dao;

import modelo.Persona;
import modelo.RegistroAcceso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    public void registrar(Persona p) throws SQLException {
        String sql = "INSERT INTO personas(nombres,tipo,empresa,identificador_biometrico,activa) VALUES(?,?,?,?,?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombres()); ps.setString(2, p.getTipo());
            ps.setString(3, p.getEmpresa()); ps.setString(4, p.getIdentificadorBiometrico());
            ps.setBoolean(5, p.isActiva()); ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) p.setId(rs.getInt(1)); }
        }
    }

    public Persona buscarPorIdentificador(String identificador) throws SQLException {
        String sql = "SELECT * FROM personas WHERE identificador_biometrico = ?";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearPersona(rs);
            }
        }
        return null;
    }

    public List<Persona> listar() throws SQLException {
        List<Persona> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM personas ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearPersona(rs));
        }
        return lista;
    }

    public void registrarAcceso(RegistroAcceso r) throws SQLException {
        String sql = "INSERT INTO registros_acceso(persona,tipo_movimiento,resultado,detalle,fecha_hora) VALUES(?,?,?,?,?)";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getPersona()); ps.setString(2, r.getTipoMovimiento());
            ps.setString(3, r.getResultado()); ps.setString(4, r.getDetalle());
            ps.setTimestamp(5, Timestamp.valueOf(r.getFechaHora())); ps.executeUpdate();
        }
    }

    public List<RegistroAcceso> listarAccesos() throws SQLException {
        List<RegistroAcceso> lista = new ArrayList<>();
        String sql = "SELECT * FROM registros_acceso ORDER BY fecha_hora DESC";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RegistroAcceso r = new RegistroAcceso();
                r.setId(rs.getInt("id")); r.setPersona(rs.getString("persona"));
                r.setTipoMovimiento(rs.getString("tipo_movimiento")); r.setResultado(rs.getString("resultado"));
                r.setDetalle(rs.getString("detalle")); r.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
                lista.add(r);
            }
        }
        return lista;
    }

    private Persona mapearPersona(ResultSet rs) throws SQLException {
        Persona p = new Persona();
        p.setId(rs.getInt("id")); p.setNombres(rs.getString("nombres")); p.setTipo(rs.getString("tipo"));
        p.setEmpresa(rs.getString("empresa")); p.setIdentificadorBiometrico(rs.getString("identificador_biometrico"));
        p.setActiva(rs.getBoolean("activa")); return p;
    }
}
