package ec.telconet.cristhian_baidal.microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import ec.telconet.cristhian_baidal.services.ProductService;

@Component
public class LoadProducts implements CommandLineRunner {
	@Autowired
	ProductService productService;
	
	
	@Override
    public void run(String... args) throws Exception {
        String url = "https://dummyjson.com/products"; // URL del endpoint
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Llamar al endpoint y obtener los datos
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response != null && response.has("products")) {
                JsonNode products = response.get("products");
                productService.saveProductsFromAPI(products); // Guardar productos
                System.out.println("Productos cargados exitosamente.");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }
}
