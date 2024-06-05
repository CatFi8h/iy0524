package dev.yurchenko.iy0524.controller;

import dev.yurchenko.iy0524.controller.request.ToolCheckoutRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;

import static dev.yurchenko.iy0524.controller.CheckoutControllerTest.requestBody;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CheckoutControllerAcceptanceTest {
	private static final String DATE_MAPPING = "MM/dd/yy";
	private static final String URL = "/checkout";
	private static final String DISCOUNT_ERROR_MESSAGE = "Filed 'discount' should not be less than 0 and greater that 100";
	
	@Autowired
	private MockMvc mockMvc;
	
	
	@Test
	public void testCheckout_testData1_Invalid() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("JAKR", 5, 101, dateFormat.parse("9/3/15").toInstant());
		mockMvc.perform(post(URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(DISCOUNT_ERROR_MESSAGE));
	}
	
	@Test
	public void testCheckout_testData2_Valid() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("LADW", 3, 10, dateFormat.parse("7/2/20").toInstant());
		mockMvc.perform(post(URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("LADW"))
				.andExpect(jsonPath("$.brand").value("Werner"))
				.andExpect(jsonPath("$.type").value("Ladder"))
				.andExpect(jsonPath("$.days").value(3))
				.andExpect(jsonPath("$.date").value("07/02/20"))
				.andExpect(jsonPath("$.dueDate").value("07/05/20"))
				.andExpect(jsonPath("$.dailyCharge").value("$1.99"))
				.andExpect(jsonPath("$.daysCharge").value(2))
				.andExpect(jsonPath("$.preDiscountCharge").value("$3.98"))
				.andExpect(jsonPath("$.discountPercent").value("10%"))
				.andExpect(jsonPath("$.discountAmount").value("$0.40"))
				.andExpect(jsonPath("$.finalCharge").value("$3.58"));
	}
	
	@Test
	public void testCheckout_testData3_Valid() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("CHNS", 5, 25, dateFormat.parse("7/2/15").toInstant());
		mockMvc.perform(post(URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("CHNS"))
				.andExpect(jsonPath("$.brand").value("Stihl"))
				.andExpect(jsonPath("$.type").value("Chainsaw"))
				.andExpect(jsonPath("$.days").value(5))
				.andExpect(jsonPath("$.date").value("07/02/15"))
				.andExpect(jsonPath("$.dueDate").value("07/07/15"))
				.andExpect(jsonPath("$.dailyCharge").value("$1.49"))
				.andExpect(jsonPath("$.daysCharge").value(3))
				.andExpect(jsonPath("$.preDiscountCharge").value("$4.47"))
				.andExpect(jsonPath("$.discountPercent").value("25%"))
				.andExpect(jsonPath("$.discountAmount").value("$1.12"))
				.andExpect(jsonPath("$.finalCharge").value("$3.35"));
	}
	
	@Test
	public void testCheckout_testData4_Valid() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("JAKD", 6, 0, dateFormat.parse("9/3/15").toInstant());
		mockMvc.perform(post(URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("JAKD"))
				.andExpect(jsonPath("$.brand").value("DeWalt"))
				.andExpect(jsonPath("$.type").value("Jackhammer"))
				.andExpect(jsonPath("$.days").value(6))
				.andExpect(jsonPath("$.date").value("09/03/15"))
				.andExpect(jsonPath("$.dueDate").value("09/09/15"))
				.andExpect(jsonPath("$.dailyCharge").value("$2.99"))
				.andExpect(jsonPath("$.daysCharge").value(3))
				.andExpect(jsonPath("$.preDiscountCharge").value("$8.97"))
				.andExpect(jsonPath("$.discountPercent").value("0%"))
				.andExpect(jsonPath("$.discountAmount").value("$0.00"))
				.andExpect(jsonPath("$.finalCharge").value("$8.97"));
	}
	
	@Test
	public void testCheckout_testData5_Valid() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("JAKR", 9, 0, dateFormat.parse("7/2/15").toInstant());
		mockMvc.perform(post(URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("JAKR"))
				.andExpect(jsonPath("$.brand").value("Ridgid"))
				.andExpect(jsonPath("$.type").value("Jackhammer"))
				.andExpect(jsonPath("$.days").value(9))
				.andExpect(jsonPath("$.date").value("07/02/15"))
				.andExpect(jsonPath("$.dueDate").value("07/11/15"))
				.andExpect(jsonPath("$.dailyCharge").value("$2.99"))
				.andExpect(jsonPath("$.daysCharge").value(6))
				.andExpect(jsonPath("$.preDiscountCharge").value("$17.94"))
				.andExpect(jsonPath("$.discountPercent").value("0%"))
				.andExpect(jsonPath("$.discountAmount").value("$0.00"))
				.andExpect(jsonPath("$.finalCharge").value("$17.94"));
	}
	
	@Test
	public void testCheckout_testData6_Valid() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MAPPING);
		ToolCheckoutRequestDto request = new ToolCheckoutRequestDto("JAKR", 4, 50, dateFormat.parse("7/2/20").toInstant());
		mockMvc.perform(post(URL)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(requestBody(request)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("JAKR"))
				.andExpect(jsonPath("$.brand").value("Ridgid"))
				.andExpect(jsonPath("$.type").value("Jackhammer"))
				.andExpect(jsonPath("$.days").value(4))
				.andExpect(jsonPath("$.date").value("07/02/20"))
				.andExpect(jsonPath("$.dueDate").value("07/06/20"))
				.andExpect(jsonPath("$.dailyCharge").value("$2.99"))
				.andExpect(jsonPath("$.daysCharge").value(1))
				.andExpect(jsonPath("$.preDiscountCharge").value("$2.99"))
				.andExpect(jsonPath("$.discountPercent").value("50%"))
				.andExpect(jsonPath("$.discountAmount").value("$1.50"))
				.andExpect(jsonPath("$.finalCharge").value("$1.49"));
	}
}
