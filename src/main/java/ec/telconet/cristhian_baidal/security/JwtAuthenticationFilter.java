package ec.telconet.cristhian_baidal.security;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ec.telconet.cristhian_baidal.utils.JwtUtils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;




import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
    private JwtUtils jwtUtil;
	
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        
        String token = null;
        Claims claims = null;
        String email = null;
        // Verificar el encabezado de autorización
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Eliminar "Bearer "
            
            claims = jwtUtil.obtenerClaimsDelToken(token); // Extraer claims
            email = claims.get("mail", String.class);
        }

        // Si el token es válido y no está autenticado en el contexto
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        	
            if (jwtUtil.validarToken(token)) {
                var authenticationToken = jwtUtil.getAuthenticationToken(token, claims);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continuar con el filtro
        filterChain.doFilter(request, response);
    }
}
