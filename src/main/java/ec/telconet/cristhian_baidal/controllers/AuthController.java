package ec.telconet.cristhian_baidal.controllers;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ec.telconet.cristhian_baidal.dtos.request.LoginRequest;
import ec.telconet.cristhian_baidal.dtos.response.LoginResponse;
import ec.telconet.cristhian_baidal.dtos.response.Response;
import ec.telconet.cristhian_baidal.dtos.response.UsuarioDTO;
import ec.telconet.cristhian_baidal.models.RefreshToken;
import ec.telconet.cristhian_baidal.models.Usuario;
import ec.telconet.cristhian_baidal.services.RefreshTokenService;
import ec.telconet.cristhian_baidal.services.UsuarioService;
import ec.telconet.cristhian_baidal.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
    private UsuarioService usuarioService;
	
	@Autowired
	RefreshTokenService refreshTokenService;
	
	@Autowired
    private JwtUtils jwtUtils;
	
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Autenticar al usuario
        Usuario usuario = usuarioService.autenticarUsuario(loginRequest.getUserNameOrEmail(), loginRequest.getPassword(), loginRequest.getRol());
        // Construir los claims
        Map<String, Object> claims = usuarioService.construirClaims(usuario);

        // Generar el token JWT
        String token = jwtUtils.generarToken(claims);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken( usuario.getId() );
        
        
        // Convertir el empleado a DTO
        UsuarioDTO dto = usuarioService.convertirADTO(usuario);
        
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshTokenCookie.setHttpOnly(true); // No accesible desde JavaScript
        refreshTokenCookie.setSecure(true); // Solo se envía a través de HTTPS
        refreshTokenCookie.setPath("/"); // Disponible en toda la aplicación
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // Expira en 7 días
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.addCookie(refreshTokenCookie);
        // Incluir el token en la respuesta
        return ResponseEntity.ok(new LoginResponse<UsuarioDTO>(dto, token));
    }
	
	//Validar el access-token
	@GetMapping("/me")
    public ResponseEntity<?> obtenerMiInformacion(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtils.validarToken(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Claims claims = jwtUtils.obtenerClaimsDelToken(token);
        String newTokenString = jwtUtils.generarToken(claims);        
        return ResponseEntity.ok(new LoginResponse<Claims>(claims, null));
    }
	
	//Generar nuevo acces-token por refresh-token
	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refreshToken") String refreshToken){
		if (
				refreshToken == null ||
				refreshToken.isEmpty() ||
				!jwtUtils.validarRefreshToken(refreshToken)
			) {
			Response<String> response = new Response<String>("", true, "Refresh token inválido o expirado" ); 
	        return ResponseEntity.status(401).body( response );
	    }
		
		String userId = jwtUtils.obtenerUsuarioIdDelRefreshToken(refreshToken);
		
		//Validar si el refresh-token se encuentra en la base de datos.
		Optional<RefreshToken> existRefreshToken = refreshTokenService.findByToken(refreshToken);
		if( existRefreshToken.isEmpty() ) {
			Response<String> response = new Response<String>("", true, "Refresh token inválido o expirado" ); 
	        return ResponseEntity.status(401).body( response );
		}
		refreshTokenService.verifyExpiration( existRefreshToken.get() );
		// Generar un nuevo access token
	    Usuario usuario = usuarioService.obtenerUsuarioPorId(Long.parseLong(userId));
	    Map<String, Object> claims = usuarioService.construirClaims(usuario);
	    String newAccessToken = jwtUtils.generarToken(claims);
	    UsuarioDTO dto = usuarioService.convertirADTO(usuario);
	    return ResponseEntity.ok(new LoginResponse<UsuarioDTO>(dto, newAccessToken));
	}
}
