package servicio;

import dao.PersonaDAO;
import interfaces.Notificador;
import modelo.Persona;
import modelo.RegistroAcceso;

import java.sql.SQLException;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;

public class AccesoServicio {
    private static final Logger LOG = LoggerFactory.getLogger(AccesoServicio.class);
    private static final ImmutableSet<String> TIPOS_PERMITIDOS =
            ImmutableSet.of("Trabajador", "Contratista", "Visitante");
    private final PersonaDAO personaDAO;
    private final Notificador notificador;

    public AccesoServicio(PersonaDAO personaDAO, Notificador notificador) {
        this.personaDAO = personaDAO;
        this.notificador = notificador;
    }

    public String registrarPersona(String nombres, String tipo, String empresa, String identificador) throws SQLException {
        if (StringUtils.isBlank(nombres)) throw new IllegalArgumentException("Ingrese el nombre de la persona.");
        if (identificador == null || !identificador.trim().matches("[A-Za-z0-9-]{3,60}"))
            throw new IllegalArgumentException("El identificador simulado debe tener entre 3 y 60 caracteres y usar letras, números o guion.");
        if (!TIPOS_PERMITIDOS.contains(StringUtils.trimToEmpty(tipo)))
            throw new IllegalArgumentException("Seleccione un tipo de persona válido.");
        Persona persona = new Persona(nombres.trim(), tipo, empresa == null ? "" : empresa.trim(), identificador.trim());
        personaDAO.registrar(persona);
        LOG.info("Persona registrada correctamente; tipo={}", persona.getTipo());
        notificador.enviar("Se registró una persona en el sistema.");
        return "Persona registrada correctamente.";
    }

    public String simularAcceso(String identificador, String movimiento) throws SQLException {
        if (identificador == null || identificador.trim().isEmpty()) throw new IllegalArgumentException("Ingrese el identificador biométrico simulado.");
        if (!"ENTRADA".equals(movimiento) && !"SALIDA".equals(movimiento)) throw new IllegalArgumentException("Seleccione entrada o salida.");
        Persona persona = personaDAO.buscarPorIdentificador(identificador.trim());
        String nombre = persona == null ? "Identificador no registrado" : persona.getNombres();
        String resultado = persona != null && persona.isActiva() ? "AUTORIZADO" : "RECHAZADO";
        String detalle = persona == null ? "No existe una persona asociada al identificador." :
            (persona.isActiva() ? "Identidad simulada encontrada; validación académica aprobada." : "La persona está inactiva.");
        personaDAO.registrarAcceso(new RegistroAcceso(nombre, movimiento, resultado, detalle, LocalDateTime.now()));
        LOG.info("Intento de acceso procesado; movimiento={}, resultado={}", movimiento, resultado);
        notificador.enviar("Intento de acceso procesado: " + resultado);
        return resultado + ": " + detalle;
    }

    public List<Persona> listarPersonas() throws SQLException { return personaDAO.listar(); }
    public List<RegistroAcceso> listarAccesos() throws SQLException { return personaDAO.listarAccesos(); }
}
