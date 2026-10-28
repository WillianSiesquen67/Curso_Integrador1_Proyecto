# Sistema de Control de Acceso Biométrico — Planta Industrial Andina

Prototipo académico construido siguiendo el ejemplo de clase: **MVC + DAO + servicio + TDD (JUnit) + SOLID + Guava + Apache POI + Apache Commons + Logback**.
La biometría es simulada mediante un identificador ficticio; no usa lectores ni almacena huellas reales.

## Requisitos
- JDK 17 o superior.
- Apache Tomcat 9 (usa `javax.servlet`, como el ejemplo de la profesora).
- NetBeans con soporte Maven (o Maven instalado).

## Ejecutar en NetBeans
1. Descomprime el ZIP.
2. NetBeans → **File > Open Project** y selecciona la carpeta `ControlAccesoBiometricoMVC`.
3. Espera a que Maven descargue las dependencias.
4. Configura Tomcat 9 en **Tools > Servers**.
5. Clic derecho al proyecto → **Clean and Build** y luego **Run**.
6. Se abrirá la aplicación. Si no, entra a `http://localhost:8080/ControlAccesoBiometricoMVC/`.

## Ejecutar con Maven
Desde la carpeta del proyecto:
```bash
mvn clean package
```
Copia `target/ControlAccesoBiometricoMVC.war` a `TOMCAT_HOME/webapps/`, inicia Tomcat y abre:
`http://localhost:8080/ControlAccesoBiometricoMVC/`

## Base de datos
Se usa H2 en modo archivo, inicializada automáticamente en `./data/control_acceso`. No necesitas instalar MySQL.
La capa DAO centraliza las consultas y usa `PreparedStatement`.

## Datos de prueba
La aplicación registra personas, asigna un identificador biométrico simulado y permite simular un intento de acceso.
Ejemplo de identificador: `BIO-1001`. El acceso se autoriza si la persona existe y está activa y el identificador coincide.
Es una demostración simplificada: las reglas de turnos, áreas y vigencia de autorización quedan como siguiente ampliación.

## Librerías agregadas para la semana 10
- **Guava:** mantiene en una colección inmutable los tipos de persona permitidos (trabajador, contratista y visitante).
- **Apache POI:** genera el archivo Excel `bitacora_accesos.xlsx` con los registros de entrada y salida desde el botón "Exportar a Excel".
- **Apache Commons Lang:** simplifica la validación de cadenas vacías con `StringUtils`.
- **Apache Commons IO:** ayuda a copiar los datos del archivo Excel hacia la respuesta de descarga.
- **Logback + SLF4J:** registra eventos de aplicación y errores en consola y en archivos rotativos dentro de `logs/`.

## Cómo se relaciona con las diapositivas
- **MVC:** `modelo`, `vista` (JSP) y `controlador` (Servlet).
- **DAO:** `dao/PersonaDAO.java` separa persistencia y lógica de negocio.
- **TDD:** pruebas JUnit de reglas de acceso y generación del reporte Excel en `src/test`.
- **SOLID / SRP:** modelo, DAO, servicio, notificador y controlador tienen responsabilidades separadas.
- **Seguridad:** validación de entradas, consultas parametrizadas, mensajes sencillos, datos biométricos ficticios y registros que evitan incluir identificadores biométricos o credenciales.

> Nota: se prioriza que el prototipo funcione sin credenciales externas. El documento del proyecto menciona Supabase/PostgreSQL en una sección y MySQL en otra; para esta práctica se usa H2 local para que se pueda ejecutar de inmediato. La capa DAO permite cambiar después el motor de base de datos.


## Reporte de Excel
En la sección "Bitácora de accesos", pulsa **Exportar a Excel**. El navegador descargará `bitacora_accesos.xlsx`. El reporte contiene fecha y hora, persona, movimiento, resultado y detalle. Para esta versión académica se genera con `XSSFWorkbook`; si se manejaran cientos de miles de filas, convendría utilizar el modo streaming SXSSF, tal como recomienda el material de clase.

## Seguridad de dependencias
Antes de desplegar el sistema, revisar las versiones de las dependencias y sus avisos de seguridad (CVE), evitar registrar datos personales innecesarios, validar cualquier archivo recibido y configurar la rotación de logs. Esta aplicación no carga archivos del usuario; únicamente genera un Excel de salida.
