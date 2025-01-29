package ec.telconet.cristhian_baidal.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class UsuarioUpdate {
	private long id;
	private String username;
	private String password;
	private String mail;
	
	public UsuarioUpdate( Long id, String username, String password, String mail) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.mail = mail;
	}

	public long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	@NotEmpty(message = "La contraseña no puede estar vacía")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{1,8}$", message = "La contraseña debe tener al menos una letra mayúscula, un carácter especial y tener entre 1 y 8 caracteres")
	public String getPassword() {
		return password;
	}

	@NotEmpty(message = "El correo electrónico no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", message = "El correo electrónico no es válido")
	public String getMail() {
		return mail;
	}
	
	
}