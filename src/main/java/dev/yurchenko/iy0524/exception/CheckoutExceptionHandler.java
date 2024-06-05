package dev.yurchenko.iy0524.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CheckoutExceptionHandler {
	
	@ExceptionHandler({RequestValidationException.class })
	protected ResponseEntity<Object> handleRequestValidationException(RuntimeException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("message", ex.getMessage());
		
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({NoToolEntityFoundException.class})
	protected ResponseEntity<Object> handleNoToolEntityFoundException(RuntimeException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("message", ex.getMessage());
		
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}
	
}
