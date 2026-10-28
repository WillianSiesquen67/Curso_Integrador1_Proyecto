# Diseño de Arquitectura de Red e Infraestructura de Despliegue

**Sistema de Control de Acceso Biométrico - Planta Industrial Andina** **Especialidad:** Infraestructura, Redes y Seguridad

## 1\. Visión General de Infraestructura

Para garantizar un tiempo de respuesta inmediato en las garitas de ingreso de la Planta Industrial Andina y proteger los datos del personal, se ha definido una arquitectura de red local (LAN) segmentada mediante VLANs y optimizada para baja latencia.

## 2\. Segmentación de Red (VLANs)

* **VLAN 10 (Dispositivos Biométricos &amp; IoT):** Segmento restringido (192.168.10.0/24) exclusivo para las terminales y molinetes de marcación biométrica.
* **VLAN 20 (Servidores &amp; Aplicación):** Segmento de alta seguridad (192.168.20.0/24) donde se ejecuta el servidor Apache Tomcat (puerto 8080) y la base de datos H2.
* **VLAN 30 (Administración y RR.HH.):** Segmento para las estaciones de trabajo que consultan marcaciones y generan reportes en Excel.

## 3\. Matriz de Conectividad, Protocolos y Puertos

* **Servlets / Web App:** Protocolo HTTP / HTTPS | Puertos 8080 / 443 | Origen VLAN 10 y 30 -&gt; Destino VLAN 20 | Propósito: Envío de marcaciones y acceso a interfaz JSP.
* **Base de Datos H2:** Protocolo TCP | Puerto Embebido / 8082 | Origen Localhost -&gt; Destino Localhost | Propósito: Almacenamiento seguro de personas y registros.
* **Exportación POI:** Protocolo TCP | Interno | Origen Servidor -&gt; Destino Disco Local | Propósito: Generación de reportes de asistencia en formato Excel.

## 4\. Requisitos de Calidad de Servicio (QoS) y Seguridad

* **Latencia máxima permisible:** menor o igual a 50 ms entre el lector biométrico y el servidor Tomcat para evitar aglomeraciones en garita.
* **Políticas de Firewall:** Bloqueo de tráfico no autorizado hacia la VLAN de servidores y aislamiento de la red administrativa.
* **Alta Disponibilidad:** Respaldo diario del archivo de base de datos H2 en almacenamiento local.
