package ec.telconet.cristhian_baidal.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
    	.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configurar CORS
    	.csrf( csrf -> csrf.disable() )
    	// Configuración de autorización
    	.authorizeHttpRequests(auth -> auth
            .requestMatchers(
            		"/api/auth/login",
            		"/api/auth/refresh-token",
            		"/api/auth/me", 
            		"/api/rol"
            ).permitAll() // Permitir acceso a estas rutas
            .requestMatchers(
            		HttpMethod.DELETE, "/api/usuarios/{id}"
            ).hasRole("SUPERADMIN")
            .anyRequest().authenticated() // Protege	r las demás rutas
        )
        // Agregar el filtro JWT antes del filtro de autenticación predeterminado
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    	
        return http.build();
    }
    
    @Bean
	public CorsConfigurationSource  corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Cambia por la URL de tu frontend
	    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(List.of("*")); // Permitir todos los headers
	    configuration.setAllowCredentials(true); // Permitir credenciales (como cookies o cabeceras Authorization)

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Para encriptar contraseñas
    }
}