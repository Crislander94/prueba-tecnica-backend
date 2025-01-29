package ec.telconet.cristhian_baidal.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class UsuarioCreate {
	private String username;
	@NotEmpty(message = "El correo electrónico no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", message = "El correo electrónico no es válido")
	private String mail;
	@NotEmpty(message = "La contraseña no puede estar vacía")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{1,8}$", message = "La contraseña debe tener al menos una letra mayúscula, un carácter especial y tener entre 1 y 8 caracteres")
	private String password;
	private Long rolId;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getRolId() {
		return this.rolId;
	}
	public void setRolId(Long rolId) {
		this.rolId = rolId;
	}
	
}
