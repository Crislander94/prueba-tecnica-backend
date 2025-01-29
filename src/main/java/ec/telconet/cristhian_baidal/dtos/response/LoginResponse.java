package ec.telconet.cristhian_baidal.dtos.response;

public class LoginResponse<T> {
	private T usuario;
    private String token;

    public LoginResponse(T usuario, String token) {
        this.usuario = usuario;
        this.token = token;
    }

    public T getUsuario() {
        return usuario;
    }

    public String getToken() {
        return token;
    }
}
