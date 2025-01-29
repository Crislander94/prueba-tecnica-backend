package ec.telconet.cristhian_baidal.dtos.response;


import java.util.ArrayList;

import ec.telconet.cristhian_baidal.models.Usuario;

public class UsuarioErrorCsv {
	private ArrayList<String> errorsMessage;
	private Usuario usuarioFailure;
	public UsuarioErrorCsv(ArrayList<String> errorsMessage, Usuario usuarioFailure) {
		super();
		this.errorsMessage = errorsMessage;
		this.usuarioFailure = usuarioFailure;
	}
	
	public ArrayList<String> getErrorsMessage() {
		return errorsMessage;
	}
	public Usuario getUsuarioFailure() {
		return usuarioFailure;
	}
}
