package dev.yurchenko.iy0524.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.dto.ToolDto;
import dev.yurchenko.iy0524.exception.RequestValidationException;
import dev.yurchenko.iy0524.service.impl.ToolsEntityCheckoutServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ToolsEntityCheckoutServiceImpl toolsEntityCheckoutService;
	private static final ObjectMapper MAPPER = new ObjectMapper()
			                                           .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			                                           .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			                                           .registerModule(new JavaTimeModule());
	
	public static String requestBody(Object request) {
		try {
			return MAPPER.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T parseResponse(MvcResult result, Class<T> responseClass) {
		try {
			String contentAsString = result.getResponse().getContentAsString();
			return MAPPER.readValue(contentAsString, responseClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testGetToolsList() throws Exception {
		when(toolsEntityCheckoutService.getAllTools())
		.thenReturn(Stream.of(new ToolDto(1L, "CODE", "Brand", "Instrument Type", BigDecimal.TEN)).toList());
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				                      .andExpect(status().isOk())
				                      .andExpect(jsonPath("$.toolDtoList[*].id").value(1))
				                      .andExpect(jsonPath("$.toolDtoList[*].code").value("CODE"))
				                      .andExpect(jsonPath("$.toolDtoList[*].brand").value("Brand"))
				                      .andExpect(jsonPath("$.toolDtoList[*].type").value("Instrument Type"))
				                      .andExpect(jsonPath("$.toolDtoList[*].price").value(10));
	}
	
	@Test
	public void testCheckoutTools() throws Exception {
		RentalAgreementResponse rentalAgreementResponse = new RentalAgreementResponse("CODE",
				"Brand",
				"Instrument Type",
				10,
				Date.from(Instant.parse("2007-12-03T10:15:30.00Z")),
				Date.from(Instant.parse("2007-12-14T10:15:30.00Z")),
				BigDecimal.TEN,
				10,
				BigDecimal.TWO,
				14,
				new BigDecimal(25),
				new BigDecimal(124));
		when(toolsEntityCheckoutService.createRentalAgreementResponse(any(ToolRequest.class)))
				.thenReturn(rentalAgreementResponse);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ToolRequest request = new ToolRequest("CODE", 5, 15, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post("/checkout")
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("CODE"))
				.andExpect(jsonPath("$.brand").value("Brand"))
				.andExpect(jsonPath("$.type").value("Instrument Type"))
				.andExpect(jsonPath("$.days").value(10))
				.andExpect(jsonPath("$.date").value("2007-12-03T10:15:30.000+00:00"))
				.andExpect(jsonPath("$.dueDate").value("2007-12-14T10:15:30.000+00:00"))
				.andExpect(jsonPath("$.dailyCharge").value(10))
				.andExpect(jsonPath("$.daysCharge").value(10))
				.andExpect(jsonPath("$.preDiscountCharge").value(2))
				.andExpect(jsonPath("$.discountPercent").value(14))
				.andExpect(jsonPath("$.discountAmount").value(25))
				.andExpect(jsonPath("$.finalCharge").value(124));
	
	}
	
	@Test
	public void test_createRentalAgreementResponse_NoCodeInRequest() throws Exception {
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertThrows(RequestValidationException.class,
				() -> new ToolRequest(null, 5, -15, dateFormat.parse("2007-12-03").toInstant()),
				"Field 'code' should not be Blank and less nor bigger length 4");
		
	}
	@Test
	public void test_createRentalAgreementResponse_DaysLessThanOne() throws Exception {
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertThrows(RequestValidationException.class,
				() -> new ToolRequest("CODE", 0, -15, dateFormat.parse("2007-12-03").toInstant()),
				"Field 'days' should not be less than 1.");
		
	}
	@Test
	public void test_createRentalAgreementResponse_discountLessThanZero() throws Exception {
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertThrows(RequestValidationException.class,
				() -> new ToolRequest("CODE", 5, -1, dateFormat.parse("2007-12-03").toInstant()),
				"Filed 'discount' should not be less than 0 and greater that 100");
		
	}
	@Test
	public void test_createRentalAgreementResponse_checkoutDateIsNull() throws Exception {
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertThrows(RequestValidationException.class,
				() -> new ToolRequest(null, 5, -15, null),
				"Filed 'checkoutDate' should not be empty");
		
	}
	

}