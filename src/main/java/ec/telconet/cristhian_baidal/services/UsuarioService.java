package ec.telconet.cristhian_baidal.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import ec.telconet.cristhian_baidal.dtos.request.UsuarioCreate;
import ec.telconet.cristhian_baidal.dtos.request.UsuarioUpdate;
import ec.telconet.cristhian_baidal.dtos.response.UserCSVValidate;
import ec.telconet.cristhian_baidal.dtos.response.UsuarioDTO;
import ec.telconet.cristhian_baidal.dtos.response.UsuarioErrorCsv;
import ec.telconet.cristhian_baidal.excepciones.UsuarioExcepciones;
import ec.telconet.cristhian_baidal.models.Rol;
import ec.telconet.cristhian_baidal.models.RolUsuario;
import ec.telconet.cristhian_baidal.models.Usuario;
import ec.telconet.cristhian_baidal.repositories.RolRepository;
import ec.telconet.cristhian_baidal.repositories.RolUsuarioRepositorio;
import ec.telconet.cristhian_baidal.repositories.UsuarioRepository;


@Service
public class UsuarioService {
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private RolRepository rolRepository;
	
	@Autowired
	private RolUsuarioRepositorio rolUsuarioRepositorio;
	
	
	public Usuario autenticarUsuario(String correo, String password, int rol) {
        Usuario usuario = usuarioRepository.selectUsuarioByUserNameOrEmail(correo, rol)
                .orElseThrow(() -> new UsuarioExcepciones("Credenciales incorrectas"));
        
        if (!usuario.getPassword().equals(password)) {
            throw new UsuarioExcepciones("Credenciales incorrectas");
        }

        return usuario;
    }
	
	public Map<String, Object> construirClaims(Usuario usuario) {
		String firstRoleName = usuario.getRoles().iterator().next().getName();
       return Map.of(
           "id", usuario.getId(),
           "username", usuario.getUsername(),
           "mail", usuario.getMail(),
           "rol",firstRoleName
       );
   }
	
	public UsuarioDTO convertirADTO(Usuario  usuario) {
		UsuarioDTO dto = new UsuarioDTO();
		String firstRoleName = usuario.getRoles().iterator().next().getName();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setMail(usuario.getMail());
        dto.setRol(firstRoleName);
        return dto;
    }
	
	public Page<UsuarioDTO> obtenerTodosLosUsuarios(int page, int size, String termino) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Usuario> usuarioPaginado =  usuarioRepository.findAllByEstado( 1, termino, pageable );
		//return usuarioPaginado;
		return usuarioPaginado.map(this::convertirADTO);
    }
	
	public Usuario obtenerUsuarioPorId(Long id) {
        Optional<Usuario> empleado = usuarioRepository.findByIdAndEstado(id, 1);
        if (empleado.isEmpty()) {
            throw new RuntimeException("Empleado con ID " + id + " no encontrado.");
        }
        return empleado.get();
    }
	
	public Usuario crearUsuarios(UsuarioCreate usuario) {
        // Verificar si el rol existe
		Long rolId = usuario.getRolId();
        Optional<Rol> rolOptional = rolRepository.findById( rolId );
        if (rolOptional.isEmpty()) {
            throw new RuntimeException("El rol con ID " + rolId + " no existe.");
        }
        
        // Verificar si ya existe un usuario con el mismo username o correo
        if (usuarioRepository.existsByUsernameAndEstado(usuario.getUsername(), 1)) {
            throw new UsuarioExcepciones("El username '" + usuario.getUsername() + "' ya está en uso.");
        }
        if (usuarioRepository.existsByMail(usuario.getMail())) {
            throw new UsuarioExcepciones("El correo '" + usuario.getMail() + "' ya está en uso.");
        }
        
        Usuario newUsuario = new Usuario();
        Set<Rol> selectedRolesSet = new HashSet<>();
        selectedRolesSet.add(rolOptional.get() );
        newUsuario.setUsername( usuario.getUsername() );
        newUsuario.setMail( usuario.getMail() );
        newUsuario.setPassword( usuario.getPassword() );
        newUsuario.setRoles(selectedRolesSet );
        // Guardar el usuario en la base de datos
        return usuarioRepository.save(newUsuario);
        
    }
	
	public Usuario actualizarUsuario( UsuarioUpdate usuario) {
		// Verificar si el rol existe
		Long id = usuario.getId();
        // Verificar si ya existe un usuario con el mismo username que sea Diferente al id
        if( !usuarioRepository.existsById( id) ) {
        	throw new UsuarioExcepciones("No existe un usario con ese id.");
        }
        if ( usuarioRepository.existsUserByUserName( usuario.getUsername(), id ) > 0 ) {
            throw new UsuarioExcepciones("El username '" + usuario.getUsername() + "' ya está en uso.");
        }
        if (usuarioRepository.existsUserByMail(usuario.getMail(), id ) > 0 ) {
            throw new UsuarioExcepciones("El correo '" + usuario.getMail() + "' ya está en uso.");
        }
        Usuario updateUsuario = usuarioRepository.findById(id).get();
        
        updateUsuario.setUsername( usuario.getUsername() );
        updateUsuario.setMail( usuario.getMail() );
        updateUsuario.setPassword( usuario.getPassword() );
        // Actualizar el usuario en la base de datos
        Usuario usuarioActualizado =  usuarioRepository.save(updateUsuario);
        
        return updateUsuario;
        
	}
	
	public void eliminarUsuario(Long id) {
	    // Verificar si el usuario existe
	    Usuario usuarioExistente = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("El usuario con ID " + id + " no existe."));

	    // Cambiar el estado a 0
	    usuarioExistente.setEstado(0);

	    // Guardar el cambio en la base de datos
	    usuarioRepository.save(usuarioExistente);
	}

	public Optional<List<UsuarioDTO>> ultimosCincoUsuarios(){
		Optional<List<Usuario>> usuarios = usuarioRepository.findUserLastFive();
		if( usuarios.isEmpty()) {
			return Optional.empty();
		}
		List<UsuarioDTO> usuariosDto = usuarios.get().stream()
										.map( this::convertirADTO).toList();
		
		return Optional.of( usuariosDto );
	}
	
	public UserCSVValidate saveUserFromCsv(MultipartFile file){
        try(
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader( file.getInputStream(), StandardCharsets.UTF_8 )
            )
        ){
            String line;
            ArrayList<Usuario> usuarioList = new ArrayList<>();
            UserCSVValidate validateProcessCSVUsers;
            ArrayList<UsuarioErrorCsv> usersFailure = new ArrayList<>();
            String regexpMail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
            String regexpPassword = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{1,8}$";
            Pattern patronMail = Pattern.compile(regexpMail);
            Pattern patronPassword = Pattern.compile(regexpPassword);
            List<Rol> roles = rolRepository.findAll();
            reader.readLine();
            while(( line = reader.readLine()) != null ){
                String[] fields = line.split(",");
                Long rol;
                ArrayList<String> errorMessage = new ArrayList<String>();
                if( fields.length < 4 ) {
                	throw new UsuarioExcepciones("Archivo CSV inválido revise los datos");
                }
                Usuario usuario = new Usuario();
                usuario.setUsername(fields[0]);
                usuario.setPassword(fields[1]);
                usuario.setMail(fields[2]);
                rol = Long.parseLong(fields[3]);
	
                Matcher matcherMail = patronMail.matcher( usuario.getMail() );
                Matcher matcherPassword = patronPassword.matcher( usuario.getPassword() );
                Optional<Rol> areaOptional = rolRepository.findById( rol );
                if (areaOptional.isEmpty()) {
                	errorMessage.add("El rol con ID " + rol + " no existe.");
                }else {
                	List<Rol> selectedRolesList = roles.stream().filter( r -> r.getId() == rol ).toList();
                    Set<Rol> selectedRolesSet = new HashSet<>(selectedRolesList);
                    usuario.setRoles(selectedRolesSet);
                }
                if( !matcherMail.matches() ) {
                	errorMessage.add("El Mail: " + usuario.getMail() + " no tiene un formato valido");
                }
                if( !matcherPassword.matches() ) {
                	errorMessage.add("La contraseña: " + usuario.getPassword() + " no tiene un formato valido");
                }
                if (usuarioRepository.existsByUsernameAndEstado(usuario.getUsername(), 1)) {
                   errorMessage.add("El username: " + usuario.getUsername() + " existe");
                }
                if (usuarioRepository.existsByMail(usuario.getMail())) {
                	errorMessage.add("El email: " + usuario.getMail() + " existe");
                }
                
                if( errorMessage.size() > 0) {
                	UsuarioErrorCsv userError = new UsuarioErrorCsv(errorMessage, usuario);
                	usersFailure.add( userError);
                }else {
                	
                	usuarioList.add( usuario );
                }
            }
            usuarioRepository.saveAll( usuarioList );
            validateProcessCSVUsers = new UserCSVValidate( 
        			usuarioList.size(),
        			usersFailure.size(),
        			usuarioList,
        			usersFailure
        		);
            
            return validateProcessCSVUsers;
        } catch( UsuarioExcepciones e) {
        	throw new UsuarioExcepciones("Archivo CSV inválido revise los datos");
        }
        catch (Exception e) {
        	System.out.println( e.getMessage() );
            throw new RuntimeException("Error al procesar el archivo CSV: ");
        }
    }

	public List<Usuario> obtenerUsuarios(){
		try {
			List<Usuario> usuarios = usuarioRepository.findAll();
			return usuarios;
		}catch(UsuarioExcepciones e) {
			System.out.println(e.getMessage());
			throw new UsuarioExcepciones("Error al procesar el PDF");
		}catch (RuntimeException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException("Error al procesar el PDF");
		}
	}

	public RolUsuario agregarRolUsuario( Long idUsuario, Long idRol) {
		if( rolUsuarioRepositorio.existsByIdUsuarioAndIdRol( idUsuario, idRol) ) {
			throw new UsuarioExcepciones("Este usuario ya cuenta con este rol");
		}
		
		if(!rolRepository.existsById(idRol)) {
			throw new UsuarioExcepciones("El rol no existe");
		}
		RolUsuario newRolUsuario = new RolUsuario();
		newRolUsuario.setIdUsuario(idUsuario);
		newRolUsuario.setIdRol(idRol);
		
		return rolUsuarioRepositorio.save( newRolUsuario );
	}
}
