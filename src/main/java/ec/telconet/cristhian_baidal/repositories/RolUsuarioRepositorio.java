package ec.telconet.cristhian_baidal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.telconet.cristhian_baidal.models.RolUsuario;

public interface RolUsuarioRepositorio extends JpaRepository<RolUsuario, Long> {
	
	boolean existsByIdUsuarioAndIdRol( Long idUsuario, Long idRol);
}
