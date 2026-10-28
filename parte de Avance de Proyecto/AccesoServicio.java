package servicio;

import dao.PersonaDAO;
import interfaces.Notificador;
import modelo.Persona;
import modelo.RegistroAcceso;
import modelo.Autorizacion;
import modelo.Incidencia;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeParseException;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/** Reglas de negocio independientes de JSP y Servlets. */
public class AccesoServicio {
    private static final Logger LOG = LoggerFactory.getLogger(AccesoServicio.class);
    private static final ImmutableSet<String> TIPOS_PERMITIDOS =
            ImmutableSet.of("Trabajador", "Contratista", "Visitante");
    private static final ImmutableSet<String> TIPOS_INCIDENCIA =
            ImmutableSet.of("Seguridad", "Dispositivo", "Observación");
    private final PersonaDAO personaDAO;
    private final Notificador notificador;
    private final Clock reloj;

    public AccesoServicio(PersonaDAO personaDAO, Notificador notificador) {
        this(personaDAO, notificador, Clock.systemDefaultZone());
    }
    public AccesoServicio(PersonaDAO personaDAO, Notificador notificador, Clock reloj) {
        this.personaDAO = personaDAO;
        this.notificador = notificador;
        this.reloj = reloj;
    }

    public String registrarPersona(String nombres, String tipo, String empresa, String identificador) throws SQLException {
        String nombre = requerido(nombres, "Ingrese el nombre de la persona.", 120);
        String codigo = requerido(identificador, "Ingrese el identificador biométrico simulado.", 60);
        if (!codigo.matches("[A-Za-z0-9-]{3,60}"))
            throw new IllegalArgumentException("El identificador debe tener de 3 a 60 caracteres: letras, números o guion.");
        if (!TIPOS_PERMITIDOS.contains(StringUtils.trimToEmpty(tipo)))
            throw new IllegalArgumentException("Seleccione un tipo de persona válido.");
        String empresaLimpia = StringUtils.trimToEmpty(empresa);
        if (empresaLimpia.length() > 120) throw new IllegalArgumentException("La empresa debe tener como máximo 120 caracteres.");
        Persona persona = new Persona(nombre, tipo, empresaLimpia, codigo.toUpperCase(java.util.Locale.ROOT));
        try { personaDAO.registrar(persona); }
        catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new IllegalArgumentException("Ese identificador simulado ya está registrado.");
            throw e;
        }
        LOG.info("Persona registrada: id={}, tipo={}", persona.getId(), persona.getTipo());
        notificador.enviar("Se registró una persona en el sistema.");
        return "Persona registrada correctamente.";
    }

    public String simularAcceso(String identificador, String movimiento) throws SQLException {
        return simularAcceso(identificador, movimiento, "Principal");
    }

    public String simularAcceso(String identificador, String movimiento, String area) throws SQLException {
        String codigo = requerido(identificador, "Ingrese el identificador biométrico simulado.", 60);
        if (!codigo.matches("[A-Za-z0-9-]{3,60}")) throw new IllegalArgumentException("Identificador inválido.");
        if (!"ENTRADA".equals(movimiento) && !"SALIDA".equals(movimiento))
            throw new IllegalArgumentException("Seleccione entrada o salida.");
        String areaLimpia = requerido(area, "Seleccione un área.", 120);
        Persona persona = personaDAO.buscarPorIdentificador(codigo.toUpperCase(java.util.Locale.ROOT));
        String nombre = persona == null ? "Persona no identificada" : persona.getNombres();
        String resultado = "RECHAZADO";
        String detalle;
        LocalDateTime instante = LocalDateTime.now(reloj);
        if (persona == null) detalle = "Identificador no registrado.";
        else if (!persona.isActiva() && "ENTRADA".equals(movimiento)) detalle = "La persona está inactiva.";
        else {
            String ultimo = personaDAO.ultimoMovimientoAutorizado(persona.getId());
            if ("ENTRADA".equals(movimiento)) {
                if ("ENTRADA".equals(ultimo)) detalle = "La persona ya registra una entrada sin salida.";
                else if (!personaDAO.estaAutorizada(persona.getId(),areaLimpia,instante))
                    detalle = "No cuenta con autorización vigente para el área y horario.";
                else { resultado = "AUTORIZADO"; detalle = "Identidad y autorización simuladas validadas."; }
            } else if (!"ENTRADA".equals(ultimo)) detalle = "No se registra una entrada pendiente de salida.";
            else { resultado = "AUTORIZADO"; detalle = "Salida registrada correctamente."; }
        }
        RegistroAcceso registro = new RegistroAcceso(nombre,movimiento,resultado,detalle,instante);
        registro.setArea(areaLimpia);
        if (persona != null) registro.setPersonaId(persona.getId());
        personaDAO.registrarAcceso(registro);
        LOG.info("Intento de acceso procesado; movimiento={}, resultado={}",movimiento,resultado);
        notificador.enviar("Intento de acceso procesado: " + resultado);
        return resultado + ": " + detalle;
    }

    public String registrarArea(String nombre) throws SQLException {
        String area = requerido(nombre,"Ingrese el nombre del área.",120);
        try { personaDAO.registrarArea(area); }
        catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new IllegalArgumentException("Esa área ya existe.");
            throw e;
        }
        return "Área registrada correctamente.";
    }

    public String autorizarPersona(String personaId, String area, String desde, String hasta, String inicio, String fin) throws SQLException {
        int id = enteroPositivo(personaId);
        String areaLimpia = requerido(area,"Seleccione un área.",120);
        try {
            LocalDate fechaInicio = LocalDate.parse(desde);
            LocalDate fechaFin = LocalDate.parse(hasta);
            LocalTime horaInicio = LocalTime.parse(inicio);
            LocalTime horaFin = LocalTime.parse(fin);
            if (fechaFin.isBefore(fechaInicio) || !horaFin.isAfter(horaInicio))
                throw new IllegalArgumentException("La fecha final debe ser igual o posterior, y la hora final mayor que la inicial.");
            if (fechaFin.isAfter(LocalDate.now(reloj).plusYears(2)))
                throw new IllegalArgumentException("La autorización no debe extenderse más de dos años desde hoy.");
            personaDAO.autorizar(id,areaLimpia,fechaInicio,fechaFin,horaInicio,horaFin);
            return "Autorización registrada para el área seleccionada.";
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Revise las fechas y horas de la autorización.");
        } catch (SQLException e) {
            if ("23506".equals(e.getSQLState())) throw new IllegalArgumentException("La persona seleccionada no existe.");
            throw e;
        }
    }

    public String cambiarEstado(String personaId, boolean activa) throws SQLException {
        personaDAO.cambiarEstado(enteroPositivo(personaId),activa);
        return activa ? "Persona activada." : "Persona desactivada.";
    }
    public String revocarAutorizacion(String id) throws SQLException {
        personaDAO.revocar(enteroPositivo(id)); return "Autorización revocada.";
    }
    public String registrarIncidencia(String tipo, String descripcion) throws SQLException {
        if (!TIPOS_INCIDENCIA.contains(tipo)) throw new IllegalArgumentException("Seleccione un tipo de incidencia válido.");
        personaDAO.registrarIncidencia(tipo,requerido(descripcion,"Describa la incidencia.",400));
        return "Incidencia registrada correctamente.";
    }

    public List<Persona> listarPersonas() throws SQLException { return personaDAO.listar(); }
    public List<RegistroAcceso> listarAccesos() throws SQLException { return personaDAO.listarAccesos(); }
    public List<String> listarAreas() throws SQLException { return personaDAO.listarAreas(); }
    public List<String> personasDentro() throws SQLException { return personaDAO.personasDentro(); }
    public List<Autorizacion> listarAutorizaciones() throws SQLException { return personaDAO.listarAutorizaciones(); }
    public List<Incidencia> listarIncidencias() throws SQLException { return personaDAO.listarIncidencias(); }

    private static int enteroPositivo(String valor) {
        try { int id=Integer.parseInt(valor); if (id>0) return id; }
        catch (NumberFormatException ignored) {}
        throw new IllegalArgumentException("Seleccione un registro válido.");
    }
    private static String requerido(String valor,String mensaje,int max) {
        if (StringUtils.isBlank(valor)) throw new IllegalArgumentException(mensaje);
        String limpio=valor.trim();
        if (limpio.length()>max) throw new IllegalArgumentException("El campo supera " + max + " caracteres.");
        return limpio;
    }
}
