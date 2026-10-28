package servicio;

import modelo.RegistroAcceso;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/** Genera un reporte .xlsx de la bitácora usando Apache POI. */
public class ReporteExcelServicio {
    public byte[] generarReporteAccesos(List<RegistroAcceso> accesos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = workbook.createSheet("Bitacora de accesos");
            String[] encabezados = {"Fecha y hora", "Persona", "Movimiento", "Resultado", "Detalle", "Área"};

            CellStyle estiloEncabezado = workbook.createCellStyle();
            Font fuente = workbook.createFont();
            fuente.setBold(true);
            estiloEncabezado.setFont(fuente);

            Row filaEncabezado = hoja.createRow(0);
            for (int i = 0; i < encabezados.length; i++) {
                Cell celda = filaEncabezado.createCell(i);
                celda.setCellValue(encabezados[i]);
                celda.setCellStyle(estiloEncabezado);
            }

            int numeroFila = 1;
            for (RegistroAcceso acceso : accesos) {
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(acceso.getFechaHora().toString().replace('T', ' '));
                fila.createCell(1).setCellValue(valorSeguro(acceso.getPersona()));
                fila.createCell(2).setCellValue(valorSeguro(acceso.getTipoMovimiento()));
                fila.createCell(3).setCellValue(valorSeguro(acceso.getResultado()));
                fila.createCell(4).setCellValue(valorSeguro(acceso.getDetalle()));
                fila.createCell(5).setCellValue(valorSeguro(acceso.getArea()));
            }
            for (int i = 0; i < encabezados.length; i++) hoja.autoSizeColumn(i);
            workbook.write(salida);

            // Commons IO copia los bytes del libro al arreglo de salida.
            ByteArrayOutputStream copia = new ByteArrayOutputStream();
            IOUtils.copy(new ByteArrayInputStream(salida.toByteArray()), copia);
            return copia.toByteArray();
        }
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "");
    }
}
