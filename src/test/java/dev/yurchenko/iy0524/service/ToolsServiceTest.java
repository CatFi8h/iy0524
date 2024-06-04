package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.entites.BrandEntity;
import dev.yurchenko.iy0524.entites.ToolEntity;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.repository.ToolRepository;
import dev.yurchenko.iy0524.service.dto.DateCheckout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ToolsService.class})
class ToolsServiceTest {
	
	@MockBean
	private ToolRepository toolRepository;
	@MockBean
	private DateCheckService dateCheckService;
	@Autowired
	private ToolsService toolsService;

	
	@Test
	public void testCheckoutValidateRequest_DaysZero_Invalid() {
		when(toolRepository.checkoutWith(any())).thenThrow(IllegalArgumentException.class);
		ToolRequest toolRequest = new ToolRequest("CODE", 0, 14, "TODAY");
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> toolsService.checkout(toolRequest));
		
	}
	
	@Test
	public void testCheckoutValidateRequest_DaysNull_Invalid() {
		when(toolRepository.checkoutWith(any())).thenThrow(IllegalArgumentException.class);
		ToolRequest toolRequest = new ToolRequest("CODE", null, 14, "TODAY");
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> toolsService.checkout(toolRequest));
	}
	
	@Test
	public void testCheckoutValidateRequest_DiscountBelowZero_Invalid() {
		when(toolRepository.checkoutWith(any())).thenThrow(IllegalArgumentException.class);
		ToolRequest toolRequest = new ToolRequest("CODE", 11, -1, "TODAY");
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> toolsService.checkout(toolRequest));
	}
	
	@Test
	public void testCheckoutValidateRequest_DiscountMoreThan100_Invalid() {
		when(toolRepository.checkoutWith(any())).thenThrow(IllegalArgumentException.class);
		ToolRequest toolRequest = new ToolRequest("CODE", 11, 101, "TODAY");
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> toolsService.checkout(toolRequest));
	}
	
	@Test
	public void testCheckoutRequest_NoToolsFoundByCode_Invalid() {
		when(toolRepository.checkoutWith(any())).thenReturn(null);
		ToolRequest toolRequest = new ToolRequest("CODE", 11, 101, "TODAY");
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> toolsService.checkout(toolRequest));
	}
	
	@Test
	public void testCheckoutRequest_CheckDueDate_Valid() throws ParseException {
		BrandEntity brand = new BrandEntity();
		brand.setId(1L);
		brand.setName("My Brand");
		ToolTypeEntity toolType = new ToolTypeEntity();
		toolType.setId(1L);
		toolType.setName("My Tool Type");
		toolType.setDailyCharge(BigDecimal.ONE);
		toolType.setHolidayCharge(true);
		toolType.setWeekendCharge(true);
		toolType.setWeekdayCharge(true);
		ToolEntity tool = new ToolEntity();
		tool.setId(1L);
		tool.setCode("CODE");
		tool.setBrand(brand);
		tool.setToolType(toolType);
		DateCheckout dateCheckout = new DateCheckout(11,
				10,
				1,
				DateFormat.getDateInstance().parse("Sun Sep 01 00:00:00 PDT 2021"),
				DateFormat.getDateInstance().parse("Sun Sep 12 00:00:00 PDT 2021"));
		
		when(toolRepository.checkoutWith(anyString())).thenReturn(tool);
		when(dateCheckService.getCheckoutDateFromDate(anyString(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(dateCheckout);
		ToolRequest toolRequest = new ToolRequest("CODE", 11, 20, "2021-09-01 00:00:00");
		
		RentalAgreementResponse checkout = toolsService.checkout(toolRequest);
		
		assertNotNull(checkout);
		assertEquals("Sun Sep 12 00:00:00 PDT 2021", checkout.dueDate().toString());
		assertEquals("Wed Sep 01 00:00:00 PDT 2021", checkout.date().toString());
		assertEquals("CODE", checkout.code());
		assertEquals("My Brand", checkout.brand());
		assertEquals(BigDecimal.ONE, checkout.dailyCharge());
		assertEquals(BigDecimal.valueOf(10).setScale(2,RoundingMode.HALF_UP), checkout.preDiscountCharge());
		assertEquals(BigDecimal.valueOf(2).setScale(2, RoundingMode.HALF_UP), checkout.discountAmount());
		assertEquals(BigDecimal.valueOf(8).setScale(2,RoundingMode.HALF_UP), checkout.finalCharge());
		assertEquals(20, checkout.discountPercent());
		
	}
	
	
	
}