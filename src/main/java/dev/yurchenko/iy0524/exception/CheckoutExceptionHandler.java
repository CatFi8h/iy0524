package dev.yurchenko.iy0524.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.text.ParseException;

@ControllerAdvice
public class CheckoutExceptionHandler {
	
	@ExceptionHandler(value = IllegalArgumentException.class)
	protected ResponseEntity<Object> handleValidation(Exception ex, WebRequest request) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatusCode.valueOf(400));
	}
	
	@ExceptionHandler(value = ParseException.class)
	protected ResponseEntity<Object> handleDateParsing(Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatusCode.valueOf(400));
	}
}
