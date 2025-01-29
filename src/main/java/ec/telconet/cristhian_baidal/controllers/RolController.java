package ec.telconet.cristhian_baidal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ec.telconet.cristhian_baidal.models.Rol;
import ec.telconet.cristhian_baidal.services.RolService;

@RestController
@RequestMapping("/api/rol")
public class RolController {
	@Autowired
	RolService rolService;
	
	@GetMapping
	public ResponseEntity<?> obtenerRol() {
        List<Rol> roles = rolService.obtenerRoles();
        return ResponseEntity.ok( roles );
    }
}
