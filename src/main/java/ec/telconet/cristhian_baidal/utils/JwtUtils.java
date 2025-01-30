package ec.telconet.cristhian_baidal.utils;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Collections;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtils {
	private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Cambia esto por algo más seguro
    private final int jwtExpirationMs = 3600000; // 1 hora
    @Value("${jwt.refresh.expiration.ms}")
    private Long jwtExpirationRefreshTokenMs;
    //= 604800000; //7 días
    
    public String generarToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecret,SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String generarRefreshToken(Long userID, Long currenTimestamp ) {
        return Jwts.builder()
                .setSubject( userID.toString() )
                .setIssuedAt(new Date())
                .setExpiration(new Date( currenTimestamp + jwtExpirationRefreshTokenMs ))
                .signWith(jwtSecret,SignatureAlgorithm.HS512)
                .compact();
    }
    
    public Claims obtenerClaimsDelToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret) // Usar Key para la validación
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean validarRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            e.printStackTrace();
        }
        return false;
    }
    
 // Obtener usuario ID del refresh token
    public String obtenerUsuarioIdDelRefreshToken(String refreshToken) {
        return Jwts.parserBuilder()
        		.setSigningKey(jwtSecret)
        		.build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .getSubject();
    }
    
    
    public UsernamePasswordAuthenticationToken getAuthenticationToken(String token, Claims claims) {
        // Obtener el email o username desde las claims
        String email = claims.get("mail", String.class); // El subject es normalmente el email o username
        String rol = claims.get("rol", String.class);
        List<GrantedAuthority> authorities = rol != null ? List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase())) : Collections.emptyList();
        // Crear el objeto de autenticación
        return new UsernamePasswordAuthenticationToken( email, claims, authorities );
    }
}
