package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.entites.BrandEntity;
import dev.yurchenko.iy0524.entites.ToolEntity;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.repository.ToolRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ToolsServiceTest {
	
	@MockBean
	private ToolRepository toolRepository;
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
	public void testCheckoutRequest_CheckDueDate_Valid() {
		BrandEntity brand = new BrandEntity();
		brand.setId(1L);
		brand.setName("My Brand");
		ToolTypeEntity toolType = new ToolTypeEntity();
		toolType.setId(1L);
		toolType.setName("My Tool Type");
		toolType.setDailyCharge(BigDecimal.ONE);
		ToolEntity tool = new ToolEntity();
		tool.setId(1L);
		tool.setBrand(brand);
		tool.setToolType(toolType);
		
		when(toolRepository.checkoutWith(any())).thenReturn(tool);
		ToolRequest toolRequest = new ToolRequest("CODE", 11, 99, "2021-09-01 00:00:00");
		
		RentalAgreementResponse checkout = toolsService.checkout(toolRequest);
		
		assertNotNull(checkout);
		assertEquals("Sun Sep 12 00:00:00 PDT 2021", checkout.dueDate().toString());
		
	}
	
	
	
}