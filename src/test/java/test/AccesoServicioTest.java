package test;

import dao.PersonaDAO;
import interfaces.Notificador;
import modelo.Persona;
import modelo.RegistroAcceso;
import org.junit.jupiter.api.Test;
import servicio.AccesoServicio;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AccesoServicioTest {
    @Test
    void identificadorRegistradoDebeAutorizarAcceso() throws Exception {
        PersonaDAO daoFalso = new PersonaDAO() {
            @Override public Persona buscarPorIdentificador(String id) {
                if ("BIO-1001".equals(id)) return new Persona("Ana Torres", "Trabajador", "Andina", id);
                return null;
            }
            @Override public void registrarAcceso(RegistroAcceso r) {}
        };
        AccesoServicio servicio = new AccesoServicio(daoFalso, mensaje -> {});
        assertTrue(servicio.simularAcceso("BIO-1001", "ENTRADA").startsWith("AUTORIZADO"));
    }

    @Test
    void identificadorDesconocidoDebeRechazarAcceso() throws Exception {
        PersonaDAO daoFalso = new PersonaDAO() {
            @Override public Persona buscarPorIdentificador(String id) { return null; }
            @Override public void registrarAcceso(RegistroAcceso r) {}
        };
        AccesoServicio servicio = new AccesoServicio(daoFalso, mensaje -> {});
        assertTrue(servicio.simularAcceso("BIO-9999", "ENTRADA").startsWith("RECHAZADO"));
    }

    @Test
    void nombreVacioDebeGenerarErrorDeValidacion() {
        AccesoServicio servicio = new AccesoServicio(new PersonaDAO(), mensaje -> {});
        assertThrows(IllegalArgumentException.class,
            () -> servicio.registrarPersona(" ", "Trabajador", "Andina", "BIO-1001"));
    }

    @Test
    void tipoPersonaNoPermitidoDebeGenerarErrorDeValidacion() {
        AccesoServicio servicio = new AccesoServicio(new PersonaDAO(), mensaje -> {});
        assertThrows(IllegalArgumentException.class,
            () -> servicio.registrarPersona("Ana Torres", "AdministradorTotal", "Andina", "BIO-1002"));
    }

    @Test
    void movimientoInvalidoDebeGenerarErrorDeValidacion() {
        AccesoServicio servicio = new AccesoServicio(new PersonaDAO(), mensaje -> {});
        assertThrows(IllegalArgumentException.class,
            () -> servicio.simularAcceso("BIO-1001", "BORRAR"));
    }
}
