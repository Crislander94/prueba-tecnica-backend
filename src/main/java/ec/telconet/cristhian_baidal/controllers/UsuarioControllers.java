package ec.telconet.cristhian_baidal.controllers;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ec.telconet.cristhian_baidal.dtos.request.UsuarioCreate;
import ec.telconet.cristhian_baidal.dtos.request.UsuarioUpdate;
import ec.telconet.cristhian_baidal.dtos.request.createRolUsuario;
import ec.telconet.cristhian_baidal.dtos.response.Response;
import ec.telconet.cristhian_baidal.dtos.response.UserCSVValidate;
import ec.telconet.cristhian_baidal.dtos.response.UsuarioDTO;
import ec.telconet.cristhian_baidal.models.RolUsuario;
import ec.telconet.cristhian_baidal.models.Usuario;
import ec.telconet.cristhian_baidal.services.ExcelService;
import ec.telconet.cristhian_baidal.services.PdfService;
import ec.telconet.cristhian_baidal.services.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControllers {
	@Autowired
	UsuarioService usuarioService;
	@Autowired
 	PdfService pdfService;
	@Autowired
	private ExcelService excelService;
	
	@PostMapping
	public ResponseEntity<Usuario> crearUsuario(
	        @Valid @RequestBody UsuarioCreate usuario
	) {
        Usuario usuarioCreado = usuarioService.crearUsuarios(usuario);
        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
	}
	/**
     * Endpoint para obtener todos los usuarios.
     *
     * @return Lista de usuarios.
     */
    @GetMapping
    public ResponseEntity<?> listarUsuarios(
	  @RequestParam(defaultValue = "0") int page,
	  @RequestParam(defaultValue = "10") int size,
	  @RequestParam(defaultValue = "") String termino
    ) {
        try {
        	Page<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios(page, size, termino);
            return ResponseEntity.ok(usuarios);
            
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener los empleados", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para obtener un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Usuario encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorID(@PathVariable Long id) {
        try {
        	Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> actualizarUsuario(
    		@Valid @RequestBody UsuarioUpdate usuarioUpdate
    ){
    	Usuario usuario =  usuarioService.actualizarUsuario(usuarioUpdate);
    	return ResponseEntity.ok( usuario );
    }
    
    @GetMapping("/ultimosCinco")
    public ResponseEntity<?> obtenerUltimosCinco(){
    	try {
    		Optional<List<UsuarioDTO>> usuarios = usuarioService.ultimosCincoUsuarios();
    		Response<List<UsuarioDTO>> response = new Response<List<UsuarioDTO>>("ok", false, usuarios.get() );
    		return  ResponseEntity.ok( response );
    	}catch (Exception e) {
			// TODO: handle exception
    		Response<String> response = new Response<String>("Error! no se pudo cargar los usuarios", true, null);
    		return new ResponseEntity<>( response, HttpStatus.INTERNAL_SERVER_ERROR );
		}
    }
    
    @PostMapping("/importCSV")
	public ResponseEntity<?> importUserFromCSV(
			@RequestParam("file") MultipartFile file
	) {
    	Response<?> response;
    	if( !file.getContentType().equals( "text/csv" )) {
    		response = new Response<String>("Solo puede importar archivos con extensión csv", true, null);  
    		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    	}
        UserCSVValidate processValidateCSV = usuarioService.saveUserFromCsv(file);
    	response = new Response<UserCSVValidate>("ok", false, processValidateCSV);
        return ResponseEntity.ok(response);
	}
    
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generarPDF() {
        // Lista de usuarios (puedes obtenerla de tu base de datos)
    	List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        // Generar el PDF
        byte[] pdfBytes = pdfService.exportarUsuariosPDF(usuarios);

        // Configurar encabezados HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=usuarios.pdf");
        headers.add("Content-Type", "application/pdf");

        // Devolver el PDF como respuesta
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/excel")
    public ResponseEntity<byte[]> generarExcel() {
        // Lista de usuarios (puedes obtenerla de tu base de datos)
    	List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        // Generar un archivo excel
        byte[] excelBytes = excelService.exportarUsuarios(usuarios);

        // Configurar encabezados HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=usuarios.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // Devolver el PDF como respuesta
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
    
    @PostMapping("/addRol")
    public ResponseEntity<?> createRolUsuario(
    		@RequestBody createRolUsuario createRolUsuario
    ){
    	RolUsuario rolUsuario =usuarioService.agregarRolUsuario(createRolUsuario.getIdUsuario(), createRolUsuario.getIdRol());
    	return ResponseEntity.ok( rolUsuario );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}