package ec.telconet.cristhian_baidal.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ec.telconet.cristhian_baidal.models.Producto;
import ec.telconet.cristhian_baidal.models.Usuario;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
	boolean existsByNombre(String nombre);
	boolean existsByNombreAndEstado(String nombre, int estado);
	
	@Query("select p from Producto p where p.estado = ?1 and (p.nombre like %?2%)")
	Page<Producto> findAllByEstado(int estado, String termino,Pageable pageable);
	Optional<Producto> findByIdAndEstado(long id, int estado);
	boolean existsById(Long id); // Verifica si existe un usuario con el id
	
	@Query(value = """
			 select * from productos where estado = 1 order by id desc limit 5;
		    		""", nativeQuery = true)
	Optional<List<Producto>> findProductLastFive();
	
	 @Query(value = """
    		 select count(*) from productos 
    		 where
    		   	estado = 1
    		   	and nombre = :nombre
    		   	and id != :id
    		 limit 1;
	""", nativeQuery = true)
    int existsProductByName( String nombre, Long id);
}