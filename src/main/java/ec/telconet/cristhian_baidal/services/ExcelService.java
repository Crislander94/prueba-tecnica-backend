package ec.telconet.cristhian_baidal.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import ec.telconet.cristhian_baidal.models.Usuario;

@Service
public class ExcelService {

	public byte[] exportarUsuarios(List<Usuario> usuarios) {
        // Crear un libro de Excel
        try (
    		Workbook workbook = new XSSFWorkbook();
    		ByteArrayOutputStream out = new ByteArrayOutputStream()
    	) {
            // Crear una hoja
            Sheet sheet = workbook.createSheet("Usuarios");

            // Crear estilos para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Crear el encabezado de la tabla
            Row headerRow = sheet.createRow(0);
            String[] columnHeaders = {"Nombre", "Email", "Estado"};
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos de usuarios
            int rowNum = 1;
            for (Usuario usuario : usuarios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(usuario.getUsername());
                row.createCell(1).setCellValue(usuario.getMail());
                row.createCell(2).setCellValue(usuario.getEstado() == 1 ? "Activo" : "Inactivo");
            }

            // Ajustar el tamaño de las columnas automáticamente
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir los datos al stream de salida
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }
}
