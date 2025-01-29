package ec.telconet.cristhian_baidal.excepciones;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ec.telconet.cristhian_baidal.dtos.response.Errors;
import ec.telconet.cristhian_baidal.dtos.response.Response;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(UsuarioExcepciones.class)
    public ResponseEntity<Response<String>> handleUsuarioException(UsuarioExcepciones ex) {
		Errors errors = new Errors();
		errors.setMsg( ex.getMessage() );
		Response<String> response = new Response<String>(errors.getMsg(), true, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( response );
    }
	
	@ExceptionHandler(ProductoExcepciones.class)
    public ResponseEntity<Response<String>> handleProductoException(ProductoExcepciones ex) {
		Errors errors = new Errors();
		errors.setMsg( ex.getMessage() );
		Response<String> response = new Response<String>(errors.getMsg(), true, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( response );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response<String>> handleRuntimeException(RuntimeException ex) {
    	Errors errors = new Errors();
		errors.setMsg( ex.getMessage() );
    	Response<String> response = new Response<String>(errors.getMsg() , true, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
