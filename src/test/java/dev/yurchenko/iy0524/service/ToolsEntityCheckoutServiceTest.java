package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.entites.BrandEntity;
import dev.yurchenko.iy0524.entites.ToolEntity;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.repository.ToolRepository;
import dev.yurchenko.iy0524.dto.DateCheckoutDto;
import dev.yurchenko.iy0524.service.impl.DateCheckServiceImpl;
import dev.yurchenko.iy0524.service.impl.ToolsEntityCheckoutServiceImpl;
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
import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ToolsEntityCheckoutServiceImpl.class})
class ToolsEntityCheckoutServiceTest {
	
	@MockBean
	private ToolRepository toolRepository;
	@MockBean
	private DateCheckService dateCheckService;
	@Autowired
	private ToolEntityCheckoutService toolsEntityCheckoutService;

	
	@Test
	public void testCreateRentalAgreementResponseRequest_CheckDueDate_Valid() throws ParseException {
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
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateCheckoutDto dateCheckoutDto = new DateCheckoutDto(11,
				10,
				1,
				dateFormat.parse("2021-09-01"),
				dateFormat.parse("2021-09-12"));
		
		when(toolRepository.getToolWithDetailsByCode(anyString())).thenReturn(Optional.of(tool));
		when(dateCheckService.getBillingDetailsFromToolTypeAndCheckoutDate(any(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(dateCheckoutDto);
		ToolRequest toolRequest = new ToolRequest("CODE", 11, 20, dateFormat.parse("2021-09-01").toInstant());
		
		RentalAgreementResponse checkout = toolsEntityCheckoutService.createRentalAgreementResponse(toolRequest);
		
		assertNotNull(checkout);
		assertEquals("Sun Sep 12 00:00:00 PDT 2021", checkout.dueDate().toString());
		assertEquals("Wed Sep 01 00:00:00 PDT 2021", checkout.date().toString());
		assertEquals(tool.getCode(), checkout.code());
		assertEquals("My Brand", checkout.brand());
		assertEquals(BigDecimal.ONE, checkout.dailyCharge());
		assertEquals(BigDecimal.valueOf(10).setScale(2,RoundingMode.HALF_UP), checkout.preDiscountCharge());
		assertEquals(BigDecimal.valueOf(2).setScale(2, RoundingMode.HALF_UP), checkout.discountAmount());
		assertEquals(BigDecimal.valueOf(8).setScale(2,RoundingMode.HALF_UP), checkout.finalCharge());
		assertEquals(20, checkout.discountPercent());
		
	}
	
	
	
}