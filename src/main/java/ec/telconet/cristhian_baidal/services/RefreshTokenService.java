package ec.telconet.cristhian_baidal.services;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ec.telconet.cristhian_baidal.models.RefreshToken;
import ec.telconet.cristhian_baidal.models.Usuario;
import ec.telconet.cristhian_baidal.repositories.RefreshTokenRepository;
import ec.telconet.cristhian_baidal.repositories.UsuarioRepository;
import ec.telconet.cristhian_baidal.utils.JwtUtils;


@Service
public class RefreshTokenService {
	@Autowired
	RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Value("${jwt.refresh.expiration.ms}")
    private Long jwtExpirationRefreshTokenMs;
	
	public RefreshToken createRefreshToken(Long userId) {
		Usuario user = usuarioRepository.findByIdAndEstado(userId, 1)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		Optional<RefreshToken> refreshTokenDB = refreshTokenRepository.findByUserID(user.getId());
		String refreshToken = jwtUtils.generarRefreshToken( userId , System.currentTimeMillis() );
		RefreshToken newRefreshToken = refreshTokenDB.isEmpty() ? new RefreshToken() : refreshTokenDB.get();	
		newRefreshToken.setExpireDate(
				Instant.now().plusMillis(jwtExpirationRefreshTokenMs)
				//ZonedDateTime.now(
				//		ZoneId.of("America/Guayaquil"))
				//.plus(java.time.Duration.ofMillis(jwtExpirationRefreshTokenMs))
				);
		newRefreshToken.setToken(refreshToken);
		if(refreshTokenDB.isEmpty()) newRefreshToken.setUser(user);
		
		return refreshTokenRepository.save(newRefreshToken);
    }
	
	public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
	
	public void verifyExpiration(RefreshToken token) {
        if (token.getExpireDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expirado");
        }
    }

	//Revocar refresh-token
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUser(usuarioRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
    }
	
	
	
}