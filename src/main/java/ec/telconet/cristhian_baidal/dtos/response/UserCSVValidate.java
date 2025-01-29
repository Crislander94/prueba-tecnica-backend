package ec.telconet.cristhian_baidal.dtos.response;

import java.util.List;

import ec.telconet.cristhian_baidal.models.Usuario;

public class UserCSVValidate {
	private int countSuccess;
	private int countFailure;
	private List<Usuario> usuariosSuccess;
	private List<UsuarioErrorCsv> usuariosFailure;
	
	public UserCSVValidate(int countSuccess, int countFailure, List<Usuario> usuariosSuccess,
			List<UsuarioErrorCsv> usuariosFailure) {
		this.countSuccess = countSuccess;
		this.countFailure = countFailure;
		this.usuariosSuccess = usuariosSuccess;
		this.usuariosFailure = usuariosFailure;
	}

	public int getCountSuccess() {
		return countSuccess;
	}

	public int getCountFailure() {
		return countFailure;
	}

	public List<Usuario> getUsuariosSuccess() {
		return usuariosSuccess;
	}

	public List<UsuarioErrorCsv> getUsuariosFailure() {
		return usuariosFailure;
	}
	
	
	
	
}
