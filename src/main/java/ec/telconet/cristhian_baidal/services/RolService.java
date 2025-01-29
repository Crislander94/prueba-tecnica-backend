package ec.telconet.cristhian_baidal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.telconet.cristhian_baidal.models.Rol;
import ec.telconet.cristhian_baidal.repositories.RolRepository;

@Service
public class RolService {

	@Autowired
	RolRepository rolRepository;
	
	
	public List<Rol> obtenerRoles() {
		List<Rol> rol = rolRepository.findAll();

        return rol;
    }
}