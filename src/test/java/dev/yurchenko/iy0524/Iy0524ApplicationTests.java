package dev.yurchenko.iy0524;

import dev.yurchenko.iy0524.controller.CheckoutController;
import dev.yurchenko.iy0524.controller.request.ToolRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class Iy0524ApplicationTests {
	
	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	@Disabled
	void contextLoads() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		ToolRequest request = new ToolRequest("JAXC", 10, 15, dateFormat.parse("2021/01/01").toInstant());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
		ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:8080/checkout", HttpMethod.POST, entity, String.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
		assertEquals("", exchange.getBody());
		
	}
	
	@Test
	@Disabled
	void contextLoads2() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		ToolRequest request = new ToolRequest("JAKR", 10, 15, dateFormat.parse("2021/01/01").toInstant());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
		ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:8080/checkout", HttpMethod.POST, entity, String.class);
		
		assertEquals(HttpStatus.OK, exchange.getStatusCode());
		assertEquals("", exchange.getBody());
		
	}

}
