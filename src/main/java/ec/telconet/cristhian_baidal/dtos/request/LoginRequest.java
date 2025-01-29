package ec.telconet.cristhian_baidal.dtos.request;

public class LoginRequest {
	private String userNameOrEmail;
    private String password;
    private int rol;

    public String getUserNameOrEmail() {
        return userNameOrEmail;
    }

    public void setUserNameOrEmail(String userNameOrEmail) {
        this.userNameOrEmail = userNameOrEmail;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public int getRol() {
		return rol;
	}

	public void setRol(int rol) {
		this.rol = rol;
	}
}