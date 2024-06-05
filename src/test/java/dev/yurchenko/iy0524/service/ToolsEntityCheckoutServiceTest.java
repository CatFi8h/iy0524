package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolCheckoutRequestDto;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponseDto;
import dev.yurchenko.iy0524.entites.BrandEntity;
import dev.yurchenko.iy0524.entites.ToolEntity;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.repository.ToolRepository;
import dev.yurchenko.iy0524.service.dto.BillingDetailsDto;
import dev.yurchenko.iy0524.service.impl.ToolsEntityCheckoutServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ToolsEntityCheckoutServiceImpl.class})
class ToolsEntityCheckoutServiceTest {
	
	@MockBean
	private ToolRepository toolRepository;
	@MockBean
	private BillingDetailsService billingDetailsService;
	@Autowired
	private ToolEntityCheckoutService toolsEntityCheckoutService;

	
	@Test
	public void testCreateRentalAgreementResponseRequest_CheckDueDate_Valid() throws ParseException {
		BrandEntity brand = new BrandEntity();
		brand.setId(1L);
		brand.setName("Brand");
		ToolTypeEntity toolType = new ToolTypeEntity();
		toolType.setId(1L);
		toolType.setName("Tool Type");
		toolType.setDailyCharge(BigDecimal.ONE);
		toolType.setHolidayCharge(true);
		toolType.setWeekendCharge(true);
		toolType.setWeekdayCharge(true);
		ToolEntity tool = new ToolEntity();
		tool.setId(1L);
		tool.setCode("CODE");
		tool.setBrand(brand);
		tool.setToolType(toolType);
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		Date checkoutDate = dateFormat.parse("09/01/21");
		Date dueDate = dateFormat.parse("09/12/21");
		BillingDetailsDto billingDetailsDto = new BillingDetailsDto(11,
				10,
				1,
				checkoutDate,
				dueDate);
		
		when(toolRepository.getToolWithDetailsByCode(anyString())).thenReturn(Optional.of(tool));
		when(billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(any(), anyInt(), any())).thenReturn(billingDetailsDto);
		ToolCheckoutRequestDto toolCheckoutRequestDto = new ToolCheckoutRequestDto("CODE", 11, 20, checkoutDate.toInstant());
		
		RentalAgreementResponseDto agreementResponse = toolsEntityCheckoutService.createRentalAgreementResponse(toolCheckoutRequestDto);
		
		assertNotNull(agreementResponse);
		assertEquals(dateFormat.format(checkoutDate), agreementResponse.date());
		assertEquals(dateFormat.format(dueDate), agreementResponse.dueDate());
		assertEquals(tool.getCode(), agreementResponse.code());
		assertEquals(brand.getName(), agreementResponse.brand());
		assertEquals(10, agreementResponse.daysCharge());
		assertEquals(11, agreementResponse.days());
		assertEquals("$1.00", agreementResponse.dailyCharge());
		assertEquals("$10.00", agreementResponse.preDiscountCharge());
		assertEquals("$2.00", agreementResponse.discountAmount());
		assertEquals("$8.00", agreementResponse.finalCharge());
		assertEquals("20%", agreementResponse.discountPercent());
		
	}
	
	
	
	
}