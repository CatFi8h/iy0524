package dev.yurchenko.iy0524.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CheckoutExceptionHandler {
	
	@ExceptionHandler(RequestValidationException.class)
	protected ResponseEntity<Object> handleValidationValidationException(RuntimeException ex, WebRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("message", ex.getMessage());
		
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}
	
}
