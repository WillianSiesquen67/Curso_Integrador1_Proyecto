package controlador;

import dao.PersonaDAO;
import interfaces.Notificador;
import notificaciones.NotificadorConsola;
import servicio.AccesoServicio;
import servicio.ReporteExcelServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AccesoServlet", urlPatterns = {"/acceso"})
public class AccesoServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(AccesoServlet.class);
    private AccesoServicio servicio;

    @Override public void init() {
        Notificador notificador = new NotificadorConsola();
        servicio = new AccesoServicio(new PersonaDAO(), notificador);
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("exportar".equals(req.getParameter("accion"))) {
            exportarReporte(resp);
            return;
        }
        cargarDatos(req);
        req.getRequestDispatcher("/WEB-INF/vistas/inicio.jsp").forward(req, resp);
    }

    private void exportarReporte(HttpServletResponse resp) throws IOException {
        try {
            byte[] archivo = new ReporteExcelServicio().generarReporteAccesos(servicio.listarAccesos());
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=bitacora_accesos.xlsx");
            resp.setContentLength(archivo.length);
            org.apache.commons.io.IOUtils.copy(new java.io.ByteArrayInputStream(archivo), resp.getOutputStream());
            LOG.info("Reporte Excel de bitácora generado correctamente");
        } catch (SQLException e) {
            LOG.error("No se pudo consultar la bitácora para exportar", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo generar el reporte.");
        } catch (IOException e) {
            LOG.error("No se pudo generar el archivo Excel", e);
            throw e;
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("registrar".equals(accion)) {
                String mensaje = servicio.registrarPersona(req.getParameter("nombres"), req.getParameter("tipo"),
                    req.getParameter("empresa"), req.getParameter("identificador"));
                req.getSession().setAttribute("mensaje", mensaje);
            } else if ("simular".equals(accion)) {
                String mensaje = servicio.simularAcceso(req.getParameter("identificadorAcceso"), req.getParameter("movimiento"));
                req.getSession().setAttribute("mensaje", mensaje);
            }
            resp.sendRedirect(req.getContextPath() + "/acceso");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            cargarDatos(req);
            req.getRequestDispatcher("/WEB-INF/vistas/inicio.jsp").forward(req, resp);
        } catch (SQLException e) {
            LOG.error("Error de base de datos al procesar una solicitud", e);
            req.setAttribute("error", "No se pudo completar la operación. Verifique los datos y vuelva a intentarlo.");
            cargarDatos(req);
            req.getRequestDispatcher("/WEB-INF/vistas/inicio.jsp").forward(req, resp);
        }
    }

    private void cargarDatos(HttpServletRequest req) throws ServletException {
        try {
            req.setAttribute("personas", servicio.listarPersonas());
            req.setAttribute("accesos", servicio.listarAccesos());
        } catch (SQLException e) {
            LOG.error("No se pudo consultar la información de la aplicación", e);
            throw new ServletException("No se pudo cargar la información de la aplicación.", e);
        }
    }
}
