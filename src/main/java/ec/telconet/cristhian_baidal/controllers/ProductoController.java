package ec.telconet.cristhian_baidal.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import ch.qos.logback.core.model.Model;
import ec.telconet.cristhian_baidal.dtos.request.ProductCreate;
import ec.telconet.cristhian_baidal.dtos.request.ProductUpdate;
import ec.telconet.cristhian_baidal.dtos.request.UsuarioCreate;
import ec.telconet.cristhian_baidal.dtos.request.UsuarioUpdate;
import ec.telconet.cristhian_baidal.dtos.response.Response;
import ec.telconet.cristhian_baidal.dtos.response.UsuarioDTO;
import ec.telconet.cristhian_baidal.models.Producto;
import ec.telconet.cristhian_baidal.models.Usuario;
import ec.telconet.cristhian_baidal.services.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductoController {
	
	@Autowired
	ProductService productoService;
	
	@GetMapping("/ultimosCinco")
    public ResponseEntity<?> obtenerUltimosCinco(){
    	try {
    		Optional<List<Producto>> productos = productoService.ultimosCincoProductos();
    		Response<List<Producto>> response = new Response<List<Producto>>("ok", false, productos.get() );
    		return  ResponseEntity.ok( response );
    	}catch (Exception e) {
			// TODO: handle exception
    		Response<String> response = new Response<String>("Error! no se pudo cargar los productos", true, null);
    		return new ResponseEntity<>( response, HttpStatus.INTERNAL_SERVER_ERROR );
		}
    }
	
	/* Cargar la data desde un store procedure */
	/*
	@PostMapping("loadData")
	public ResponseEntity<?> loadData() throws JsonMappingException, JsonProcessingException{
		
		try {
			String responseString = productoService.loadInitProducts();
			Response<String> response = new Response<String>();
			response.setData( responseString);
			response.setMessage( "Productos ingresador correctamente");
			jdbcTemplate.update("CALL sp_load_products(?)", responseString);
			return ResponseEntity.ok( response );
			
		}catch (RuntimeException e) {
			// TODO: handle exception
			System.out.println( e.getMessage());
			return new ResponseEntity<>("Error no se pudo cargar los productos", HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	*/
	@PostMapping
	public ResponseEntity<Producto> crearProducto(
	        @Valid @RequestBody ProductCreate producto
	) {
        Producto productoCreado = productoService.crearProducto(producto);
        return new ResponseEntity<>(productoCreado, HttpStatus.CREATED);
	}
	/**
     * Endpoint para obtener todos los productos.
     *
     * @return Lista de productos.
     */
    @GetMapping
    public ResponseEntity<?> listarProductos(
	  @RequestParam(defaultValue = "0") int page,
	  @RequestParam(defaultValue = "10") int size,
	  @RequestParam(defaultValue = "") String termino
    ) {
        try {
        	Page<Producto> productos = productoService.obtenerTodosLosProductos(page, size, termino);
            return ResponseEntity.ok(productos);
            
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener los productos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
	     * Endpoint para obtener un producto por su ID.
	     *
	     * @param id ID del producto.
	     * @return Producto encontrado.
    */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorID(@PathVariable Long id) {
        try {
        	Producto producto = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> actualizarUsuario(
    		@Valid @RequestBody ProductUpdate productoUpdate
    ){
    	Producto producto =  productoService.actualizarProducto(productoUpdate);
    	return ResponseEntity.ok( producto );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
        	productoService.eliminarProducto(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
}
