package dev.yurchenko.iy0524.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.yurchenko.iy0524.controller.request.ToolCheckoutRequestDto;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponseDto;
import dev.yurchenko.iy0524.service.impl.ToolsEntityCheckoutServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {
	private static final String CHECKOUT_URL = "/checkout";
	private static final String CODE = "CODE";
	private static final String DAYS_VALIDATION_ERROR_MESSAGE = "Field 'days' should not be less than 1.";
	private static final String CHECKOUT_DATE_VALIDATION_ERROR_MESSAGE = "Filed 'checkoutDate' should not be empty";
	private static final String DISCOUNT_VALIDATION_ERROR_MESSAGE = "Filed 'discount' should not be less than 0 and greater that 100";
	private static final String CODE_VALIDATION_ERROR_MESSAGE = "Field 'code' should not be Blank and less nor bigger length 4";
	private static final String DATE_MAPPING = "yyyy-MM-dd";
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
	
	@Test
	public void testCreateRentalAgreement_responseMapping_Valid() throws Exception {
		RentalAgreementResponseDto rentalAgreementResponseDto = new RentalAgreementResponseDto(CODE,
				"Brand",
				"Instrument Type",
				10,
				"12/03/07",
				"12/14/07",
				"$10.00",
				10,
				"$2",
				"14%",
				"$25.00",
				"$124.00");
		when(toolsEntityCheckoutService.createRentalAgreementResponse(any(ToolCheckoutRequestDto.class)))
				.thenReturn(rentalAgreementResponseDto);
		DateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto(CODE, 5, 15, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(CODE))
				.andExpect(jsonPath("$.brand").value("Brand"))
				.andExpect(jsonPath("$.type").value("Instrument Type"))
				.andExpect(jsonPath("$.days").value(10))
				.andExpect(jsonPath("$.date").value("12/03/07"))
				.andExpect(jsonPath("$.dueDate").value("12/14/07"))
				.andExpect(jsonPath("$.dailyCharge").value("$10.00"))
				.andExpect(jsonPath("$.daysCharge").value(10))
				.andExpect(jsonPath("$.preDiscountCharge").value("$2"))
				.andExpect(jsonPath("$.discountPercent").value("14%"))
				.andExpect(jsonPath("$.discountAmount").value("$25.00"))
				.andExpect(jsonPath("$.finalCharge").value("$124.00"));
	}
	
	
	@Test
	public void test_createRentalAgreementResponse_DaysLessThanOne() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto(CODE, 0, 15, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value(DAYS_VALIDATION_ERROR_MESSAGE));
	}
	
	@Test
	public void test_createRentalAgreementResponse_checkoutDateIsNull() throws Exception {
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto(CODE, 1, 15, null);
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value(CHECKOUT_DATE_VALIDATION_ERROR_MESSAGE));
	}
	
	@Test
	public void test_createRentalAgreementResponse_discountLessThanZero() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto(CODE, 1, -1, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value(DISCOUNT_VALIDATION_ERROR_MESSAGE));
	}
	
	@Test
	public void test_createRentalAgreementResponse_discountMoreThanZero() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto(CODE, 1, 101, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value(DISCOUNT_VALIDATION_ERROR_MESSAGE));
	}
	
	@Test
	public void test_createRentalAgreementResponse_NoCodeInRequest() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto(null, 1, 15, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value(CODE_VALIDATION_ERROR_MESSAGE));
	}
	
	@Test
	public void test_createRentalAgreementResponse_CodeIsLonger() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("CODE1", 1, 15, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value(CODE_VALIDATION_ERROR_MESSAGE));
	}
	
	@Test
	public void test_createRentalAgreementResponse_CodeIsShorter() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("COD", 1, 15, dateFormat.parse("2007-12-03").toInstant());
		mockMvc.perform(post(CHECKOUT_URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value(CODE_VALIDATION_ERROR_MESSAGE));
	}
	
	
}