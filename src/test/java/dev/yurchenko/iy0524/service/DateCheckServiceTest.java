package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.dto.DateCheckoutDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DateCheckService.class})
class DateCheckServiceTest {
	@Autowired
	private DateCheckService dateCheckService;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	@Test
	public void testCheckoutDate_dueDate_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 00:00:00").toInstant(),
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals("Thu Sep 16 00:00:00 PDT 2021", checkoutDateFromDate.dueDate().toString());
	}
	
	@Test
	public void testCheckoutDate_checkoutDate_valid() throws ParseException {
		
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 01:00:00").toInstant(),
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals("Wed Sep 01 01:00:00 PDT 2021", checkoutDateFromDate.checkoutDate().toString());
	}
	
	@Test
	public void testCheckoutDate_chargeDays_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-08-01 01:00:00").toInstant(),
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(15, checkoutDateFromDate.chargeDays());
		assertEquals(0, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 01:00:00").toInstant(),
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse1_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2024-06-01 00:00:00").toInstant(),
						7,
						true,
						false,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(5, checkoutDateFromDate.chargeDays());
		assertEquals(2, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeTrue_LaborDay_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 01:00:00").toInstant(),
						15,
						true,
						true,
						true);
		assertNotNull(checkoutDateFromDate);
		assertEquals(15, checkoutDateFromDate.chargeDays());
		assertEquals(0, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysWeekendChargeFalse_chargeDaysHolidayChargeFalse_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 01:00:00").toInstant(),
						15,
						true,
						false,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(10, checkoutDateFromDate.chargeDays());
		assertEquals(5, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_allChargeDaysFalse_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 01:00:00").toInstant(),
						15,
						false,
						false,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(0, checkoutDateFromDate.chargeDays());
		assertEquals(15, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_weekDayChargeFalse_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 01:00:00").toInstant(),
						15,
						false,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(4, checkoutDateFromDate.chargeDays());
		assertEquals(11, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnWeekend_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-07-01 01:00:00").toInstant(),
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnWorkDay_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2023-07-01 01:00:00").toInstant(),
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_LaborDay_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-09-01 01:00:00").toInstant(),
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14, checkoutDateFromDate.chargeDays());
		assertEquals(1, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-07-01 01:00:00").toInstant(),
						90,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(88, checkoutDateFromDate.chargeDays());
		assertEquals(2, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnSaturday_AND_LaborDay_ThreeYears_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2020-07-04 01:00:00").toInstant(),
						364*3,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(1087, checkoutDateFromDate.chargeDays());
		assertEquals(5, checkoutDateFromDate.freeDays());
	}
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnSaturday_AND_LaborDay_TwoYears_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2020-07-04 01:00:00").toInstant(),
						364*2,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(725, checkoutDateFromDate.chargeDays());
		assertEquals(3, checkoutDateFromDate.freeDays());
	}
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_ThreeYears_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2020-07-01 01:00:00").toInstant(),
						365*3,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(1089, checkoutDateFromDate.chargeDays());
		assertEquals(6, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_MAX_VALUE_DAYS_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-07-01 01:00:00").toInstant(),
						Integer.MAX_VALUE,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(2135724425, checkoutDateFromDate.chargeDays());
		assertEquals(11759222, checkoutDateFromDate.freeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeTrue_IndependenceDay_AND_LaborDay_MAX_VALUE_DAYS_valid() throws ParseException {
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(dateFormat.parse("2021-07-01 01:00:00").toInstant(),
						Integer.MAX_VALUE,
						true,
						true,
						true);
		assertNotNull(checkoutDateFromDate);
		assertEquals(Integer.MAX_VALUE, checkoutDateFromDate.chargeDays());
		assertEquals(0, checkoutDateFromDate.freeDays());
	}
	
}