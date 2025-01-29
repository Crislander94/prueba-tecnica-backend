package ec.telconet.cristhian_baidal.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import ec.telconet.cristhian_baidal.models.Usuario;

@Service
public class PdfService {
	public byte[] exportarUsuariosPDF(List<Usuario> usuarios) {
		// Crear un stream para almacenar el PDF en memoria
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
        try {
        	PdfWriter writer = new PdfWriter( out );
        	PdfDocument pdf = new PdfDocument( writer );
        	Document document = new Document(pdf);
        	
        	// Agregar un título al PDF
        	document.add(new Paragraph("Lista de Usuarios").setFontSize(18).setMarginBottom(20));
        	// Crear una tabla con columnas
            float[] columnWidths = {3, 3, 1}; // Ancho de las columnas
            Table table = new Table(columnWidths);
            
            // Agregar encabezados a la tabla
            table.addCell("Nombre");
            table.addCell("Email");
            table.addCell("Estado");
            
            // Agregar datos de usuarios
            for (Usuario usuario : usuarios) {
                table.addCell(usuario.getUsername());
                table.addCell(usuario.getMail());
                table.addCell(String.valueOf(usuario.getEstado() == 1 ? "Activo" : "Inactivo"));
            }
            
            // Agregar la tabla al documento
            document.add(table);

            // Cerrar el documento
            document.close();
        }catch (Exception e) {
        	e.printStackTrace();
		}
        
        return out.toByteArray();
	}
}
