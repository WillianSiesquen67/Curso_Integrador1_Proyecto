<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,modelo.Persona,modelo.RegistroAcceso" %>
<%
    List<Persona> personas = (List<Persona>) request.getAttribute("personas");
    List<RegistroAcceso> accesos = (List<RegistroAcceso>) request.getAttribute("accesos");
    String mensaje = (String) session.getAttribute("mensaje");
    session.removeAttribute("mensaje");
%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Control de Acceso | Planta Industrial Andina</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/estilos.css">
</head>
<body>
<header class="top"><div><span class="eyebrow">PLANTA INDUSTRIAL ANDINA</span><h1>Sistema de Control de Acceso</h1><p>Prototipo académico · Biometría simulada</p></div><div class="pill">● Sistema de demostración</div></header>
<main class="wrap">
<% if (mensaje != null) { %><div class="alert success"><%=escape(mensaje)%></div><% } %>
<% if (request.getAttribute("error") != null) { %><div class="alert error"><%=escape((String)request.getAttribute("error"))%></div><% } %>
<section class="stats">
<div class="stat"><span>Personas registradas</span><strong><%= personas == null ? 0 : personas.size() %></strong></div>
<div class="stat"><span>Movimientos registrados</span><strong><%= accesos == null ? 0 : accesos.size() %></strong></div>
<div class="stat"><span>Identificación</span><strong>Simulada</strong></div>
</section>
<section class="columns">
<div class="panel">
<div class="panel-title"><span class="number">01</span><div><h2>Registrar persona</h2><p>Trabajador, contratista o visitante</p></div></div>
<form method="post" action="<%=request.getContextPath()%>/acceso">
<input type="hidden" name="accion" value="registrar">
<label>Nombres y apellidos</label><input name="nombres" maxlength="120" required placeholder="Ej. Ana Torres">
<label>Tipo de persona</label><select name="tipo" required><option value="Trabajador">Trabajador</option><option value="Contratista">Contratista</option><option value="Visitante">Visitante</option></select>
<label>Empresa</label><input name="empresa" maxlength="120" placeholder="Ej. Planta Industrial Andina">
<label>Identificador biométrico simulado</label><input name="identificador" required minlength="3" maxlength="60" pattern="[A-Za-z0-9-]+" placeholder="Ej. BIO-1001">
<button type="submit">Registrar persona</button>
</form>
</div>
<div class="panel dark">
<div class="panel-title"><span class="number">02</span><div><h2>Simular acceso</h2><p>El sistema verifica el identificador ficticio</p></div></div>
<form method="post" action="<%=request.getContextPath()%>/acceso">
<input type="hidden" name="accion" value="simular">
<label>Identificador biométrico</label><input name="identificadorAcceso" required placeholder="Ej. BIO-1001">
<label>Tipo de movimiento</label><select name="movimiento"><option value="ENTRADA">Entrada a planta</option><option value="SALIDA">Salida de planta</option></select>
<button type="submit" class="light">Validar intento</button>
<p class="hint">Solo se usan identificadores de prueba; no se capturan huellas reales.</p>
</form>
</div>
</section>
<section class="panel">
<div class="section-head"><div><h2>Personas registradas</h2><p>Directorio de personal y visitantes</p></div><span class="tag"><%= personas == null ? 0 : personas.size() %> registros</span></div>
<div class="table-wrap"><table><thead><tr><th>ID</th><th>Nombre</th><th>Tipo</th><th>Empresa</th><th>ID biométrico simulado</th><th>Estado</th></tr></thead><tbody>
<% if (personas == null || personas.isEmpty()) { %><tr><td colspan="6" class="empty">Aún no hay personas. Registra una para comenzar.</td></tr><% } else { for (Persona p : personas) { %>
<tr><td><%=p.getId()%></td><td><strong><%=escape(p.getNombres())%></strong></td><td><%=escape(p.getTipo())%></td><td><%=escape(p.getEmpresa())%></td><td><code><%=escape(p.getIdentificadorBiometrico())%></code></td><td><span class="status"><%=p.isActiva()?"Activa":"Inactiva"%></span></td></tr>
<% }} %></tbody></table></div>
</section>
<section class="panel">
<div class="section-head"><div><h2>Bitácora de accesos</h2><p>Historial de intentos de entrada y salida</p></div><div class="section-actions"><span class="tag"><%= accesos == null ? 0 : accesos.size() %> eventos</span> <a class="export-link" href="<%=request.getContextPath()%>/acceso?accion=exportar">Exportar a Excel</a></div></div>
<div class="table-wrap"><table><thead><tr><th>Fecha y hora</th><th>Persona / identificador</th><th>Movimiento</th><th>Resultado</th><th>Detalle</th></tr></thead><tbody>
<% if (accesos == null || accesos.isEmpty()) { %><tr><td colspan="5" class="empty">Todavía no hay intentos de acceso registrados.</td></tr><% } else { for (RegistroAcceso r : accesos) { %>
<tr><td><%=r.getFechaHora().toString().replace('T',' ')%></td><td><%=escape(r.getPersona())%></td><td><%=escape(r.getTipoMovimiento())%></td><td><span class="<%= "AUTORIZADO".equals(r.getResultado())?"status":"status denied" %>"><%=escape(r.getResultado())%></span></td><td><%=escape(r.getDetalle())%></td></tr>
<% }} %></tbody></table></div>
</section>
<footer>Proyecto integrador · MVC + DAO + TDD + SOLID · Datos ficticios para uso académico</footer>
</main>
</body></html>
<%!
private String escape(String s) {
 if (s == null) return "";
 return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#x27;");
}
%>
