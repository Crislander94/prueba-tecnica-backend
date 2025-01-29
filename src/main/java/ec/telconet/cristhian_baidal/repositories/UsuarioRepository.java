package ec.telconet.cristhian_baidal.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fasterxml.jackson.core.sym.Name;

import ec.telconet.cristhian_baidal.models.RolUsuario;
import ec.telconet.cristhian_baidal.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query(value = """
        SELECT u.*
        FROM usuarios u
        INNER JOIN roles_usuarios ru ON u.id = ru.id_usuario
        INNER JOIN roles r ON ru.id_rol = r.id
        WHERE (u.username = :emailOrUsername OR u.mail = :emailOrUsername)
          AND u.estado = 1
          AND r.id = :rol
    """, nativeQuery = true)
	Optional<Usuario> selectUsuarioByUserNameOrEmail( 
			@Param("emailOrUsername")  String emailOrUsername,
			@Param("rol") int rol
	);

	@Query("select u from Usuario u where u.estado = ?1 and (u.mail like %?2% or u.username like %?2%)")
	Page<Usuario> findAllByEstado(int estado, String termino,  Pageable pageable);
	 
	Optional<Usuario> findByIdAndEstado(long id, int estado);
	boolean existsByUsernameAndEstado(String username, int estado); // Verifica si existe un usuario con el username dado
	boolean existsByMail(String mail); // Verifica si existe un usuario con el correo dado
	boolean existsById(Long id); // Verifica si existe un usuario con el id
    
    @Query(value = """
	 select * from usuarios where estado = 1 order by id desc limit 5;
    		""", nativeQuery = true)
    Optional<List<Usuario>> findUserLastFive();
    
    @Query(value = """
    		 select count(*) from usuarios 
    		 where
    		   	estado = 1
    		   	and username = :username
    		   	and id != :id
    		 limit 1;
	""", nativeQuery = true)
    int existsUserByUserName( String username, Long id);
    
    @Query(value = """
   		 select count(*) from usuarios 
   		 where
   		   	estado = 1
   		   	and mail = :mail
   		   	and id != :id
   		 limit 1;
	""", nativeQuery = true)
    int existsUserByMail( String mail, Long id);
}