package ec.telconet.cristhian_baidal.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ec.telconet.cristhian_baidal.models.RefreshToken;
import ec.telconet.cristhian_baidal.models.Usuario;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
	void deleteByUser(Usuario usuario);
	
	@Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userID")
	Optional<RefreshToken> findByUserID(@Param("userID") Long userID); 
}