package test;

import modelo.RegistroAcceso;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import servicio.ReporteExcelServicio;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReporteExcelServicioTest {
    @Test
    void debeGenerarExcelConEncabezadosYRegistros() throws Exception {
        RegistroAcceso registro = new RegistroAcceso("Ana Torres", "ENTRADA", "AUTORIZADO",
                "Validación académica aprobada", LocalDateTime.of(2026, 10, 22, 10, 30));
        byte[] archivo = new ReporteExcelServicio().generarReporteAccesos(List.of(registro));

        assertNotNull(archivo);
        assertTrue(archivo.length > 0);
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(archivo))) {
            assertEquals("Bitacora de accesos", workbook.getSheetAt(0).getSheetName());
            assertEquals("Fecha y hora", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            assertEquals("AUTORIZADO", workbook.getSheetAt(0).getRow(1).getCell(3).getStringCellValue());
        }
    }
}
