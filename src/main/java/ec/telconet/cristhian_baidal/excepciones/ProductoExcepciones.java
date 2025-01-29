package ec.telconet.cristhian_baidal.excepciones;

public class ProductoExcepciones extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProductoExcepciones(String mensaje) {
        super(mensaje);
    }
}
