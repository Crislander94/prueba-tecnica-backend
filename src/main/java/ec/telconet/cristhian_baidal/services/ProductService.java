package ec.telconet.cristhian_baidal.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.telconet.cristhian_baidal.dtos.request.ProductCreate;
import ec.telconet.cristhian_baidal.dtos.request.ProductUpdate;
import ec.telconet.cristhian_baidal.dtos.request.UsuarioCreate;
import ec.telconet.cristhian_baidal.dtos.request.UsuarioUpdate;
import ec.telconet.cristhian_baidal.dtos.response.UsuarioDTO;
import ec.telconet.cristhian_baidal.excepciones.ProductoExcepciones;
import ec.telconet.cristhian_baidal.excepciones.UsuarioExcepciones;
import ec.telconet.cristhian_baidal.models.Producto;
import ec.telconet.cristhian_baidal.models.Rol;
import ec.telconet.cristhian_baidal.models.RolUsuario;
import ec.telconet.cristhian_baidal.models.Usuario;
import ec.telconet.cristhian_baidal.repositories.ProductoRepository;
import jakarta.transaction.Transactional;

@Service
public class ProductService {
	@Autowired
	ProductoRepository productoRepository;
	
	private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
	
	public ProductService(RestTemplate restTemplate, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
	}
	
	public String loadInitProducts() throws JsonMappingException, JsonProcessingException {
		try {
			String response  = restTemplate.getForObject("https://dummyjson.com/products", String.class);
			//ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode products = root.get("products");
			return products.toString();
		}catch (RuntimeException e) {
			// TODO: handle exception
			throw new RuntimeException("Error al cargar los productos iniciales....");
		}
	}
	
	@Transactional
    public void saveProductsFromAPI(JsonNode products) {
        List<Producto> productosToSave = new ArrayList<Producto>();

        for (JsonNode productNode : products) {
            String title = productNode.get("title").asText();

            // Verifica si el producto ya existe por su nombre
            if (!productoRepository.existsByNombre(title)) {
                Producto producto = new Producto();
                producto.setNombre(title);
                producto.setCantidad(productNode.get("stock").asInt());
                producto.setPrecio(productNode.get("price").asDouble());
                producto.setImagen(productNode.get("thumbnail").asText());

                productosToSave.add(producto);
            }
        }

        // Guarda los productos en la base de datos
        if (!productosToSave.isEmpty()) {
            productoRepository.saveAll(productosToSave);
        }
    }
	
	public Optional<List<Producto>> ultimosCincoProductos(){
		Optional<List<Producto>> productos = productoRepository.findProductLastFive();
		return productos;
	}

	public Page<Producto> obtenerTodosLosProductos(int page, int size, String termino) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Producto> productoPaginado =  productoRepository.findAllByEstado( 1, termino, pageable );
		//return usuarioPaginado;
		return productoPaginado;
    }
	
	public Producto obtenerProductoPorId(Long id) {
        Optional<Producto> producto = productoRepository.findByIdAndEstado(id, 1);
        if (producto.isEmpty()) {
            throw new RuntimeException("Producto con ID " + id + " no encontrado.");
        }
        return producto.get();
    }
	
	public Producto crearProducto(ProductCreate producto) {        
        // Verificar si ya existe un usuario con el mismo username o correo
        if (productoRepository.existsByNombreAndEstado(producto.getNombre(), 1)) {
            throw new ProductoExcepciones("El nombre '" + producto.getNombre() + "' ya está en uso.");
        }
        
        Producto newProducto = new Producto();
        newProducto.setNombre( producto.getNombre() );
        newProducto.setCantidad( producto.getCantidad() );
        newProducto.setImagen( producto.getImagen() );
        newProducto.setPrecio( producto.getPrecio() );
        // Guardar el producto en la base de datos
        return productoRepository.save( newProducto );
    }
	
	public Producto actualizarProducto( ProductUpdate producto) {
		Long id = producto.getId();
        // Verificar si ya existe un usuario con el mismo username que sea Diferente al id
        if( !productoRepository.existsById( id) ) {
        	throw new ProductoExcepciones("No existe un usario con ese id.");
        }
        if ( productoRepository.existsProductByName( producto.getNombre(), id ) > 0 ) {
            throw new ProductoExcepciones("El nombre '" + producto.getNombre() + "' ya está en uso.");
        }
        Producto updateProducto = productoRepository.findById(id).get();
        
        updateProducto.setNombre( producto.getNombre() );
        updateProducto.setCantidad( producto.getCantidad() );
        updateProducto.setImagen( producto.getImagen() );
        updateProducto.setPrecio( producto.getPrecio() );
        // Actualizar el usuario en la base de datos
        return productoRepository.save(updateProducto);
        
	}
	
	public void eliminarProducto(Long id) {
	    // Verificar si el producto existe
	    Producto productoExistente = productoRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("El producto con ID " + id + " no existe."));

	    // Cambiar el estado a 0
	    productoExistente.setEstado(0);

	    // Guardar el cambio en la base de datos
	    productoRepository.save(productoExistente);
	}
}