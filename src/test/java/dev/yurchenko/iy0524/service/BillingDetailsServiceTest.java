package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.service.dto.BillingDetailsDto;
import dev.yurchenko.iy0524.service.impl.BillingDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BillingDetailsServiceImpl.class})
class BillingDetailsServiceTest {
	@Autowired
	private BillingDetailsService billingDetailsService;
	private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
	
	@Test
	public void testCheckoutDate_dueDate_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals("09/16/21", dateFormat.format(checkoutDateFromDate.dueDate()));
	}
	
	private static ToolTypeEntity getToolTypeEntity(boolean weekdayCharge, boolean weekendCharge, boolean holidayCharge) {
		ToolTypeEntity toolType = new ToolTypeEntity();
		toolType.setWeekdayCharge(weekdayCharge);
		toolType.setWeekendCharge(weekendCharge);
		toolType.setHolidayCharge(holidayCharge);
		return toolType;
	}
	
	@Test
	public void testCheckoutDate_checkoutDate_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals("09/01/21", dateFormat.format(checkoutDateFromDate.checkoutDate()));
	}
	
	@Test
	public void testCheckoutDate_checkoutDate11_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, false, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("06/04/24").toInstant(),
						7,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(5, checkoutDateFromDate.chargeDays());
		assertEquals(2, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDays_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("08/01/24").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(15, checkoutDateFromDate.chargeDays());
		assertEquals(0, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse1_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, false, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("06/01/24").toInstant(),
						7,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(5, checkoutDateFromDate.chargeDays());
		assertEquals(2, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeTrue_LaborDay_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, true);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(15, checkoutDateFromDate.chargeDays());
		assertEquals(0, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysWeekendChargeFalse_chargeDaysHolidayChargeFalse_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, false, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(10, checkoutDateFromDate.chargeDays());
		assertEquals(5, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_allChargeDaysFalse_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(false, false, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(0, checkoutDateFromDate.chargeDays());
		assertEquals(15, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_weekDayChargeFalse_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(false, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(4, checkoutDateFromDate.chargeDays());
		assertEquals(11, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnWeekend_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnWorkDay_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("07/01/23").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_LaborDay_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("09/01/21").toInstant(),
						15,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("07/01/21").toInstant(),
						90,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(88, checkoutDateFromDate.chargeDays());
		assertEquals(2, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnSaturday_AND_LaborDay_ThreeYears_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("07/04/20").toInstant(),
						364 * 3,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(1087, checkoutDateFromDate.chargeDays());
		assertEquals(5, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnSaturday_AND_LaborDay_TwoYears_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("07/04/20").toInstant(),
						364 * 2,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(725, checkoutDateFromDate.chargeDays());
		assertEquals(3, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_ThreeYears_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("07/01/20").toInstant(),
						365 * 3, toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(1089, checkoutDateFromDate.chargeDays());
		assertEquals(6, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_MAX_VALUE_DAYS_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, false);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("07/01/21").toInstant(),
						Integer.MAX_VALUE, toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(2135724425, checkoutDateFromDate.chargeDays());
		assertEquals(11759222, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeTrue_IndependenceDay_AND_LaborDay_MAX_VALUE_DAYS_valid() throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, true, true);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse("07/01/21").toInstant(),
						Integer.MAX_VALUE, toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(Integer.MAX_VALUE, checkoutDateFromDate.chargeDays());
		assertEquals(0, checkoutDateFromDate.freeDays());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"07/01/15", "07/02/15", "07/03/15"})
	public void testCheckoutDate_WrongCalculations1_valid(String date) throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, false, true);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse(date).toInstant(),
						5, toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(3, checkoutDateFromDate.chargeDays());
		assertEquals(2, checkoutDateFromDate.freeDays());
	}
	
	@ParameterizedTest
	@CsvSource({"07/01/15,3,2", "07/02/15,3,2", "07/03/15,3,2", "07/04/15,4,1", "07/05/15,5,0", "07/06/15,5,0", "07/07/15,4,1", "07/08/15,3,2"})
	public void testCheckoutDate_WrongCalculations2_valid(String date, int chargeDays, int freeDays) throws ParseException {
		ToolTypeEntity toolType = getToolTypeEntity(true, false, true);
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(dateFormat.parse(date).toInstant(),
						5,
						toolType);
		assertNotNull(checkoutDateFromDate);
		assertEquals(chargeDays, checkoutDateFromDate.chargeDays());
		assertEquals(freeDays, checkoutDateFromDate.freeDays());
	}
	
}