package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.service.dto.DateCheckout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DateCheckServiceTest {
	
	private final DateCheckService dateCheckService = new DateCheckService();
	
	@Test
	public void testCheckoutDate_dueDate_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals("Thu Sep 16 00:00:00 PDT 2021",checkoutDateFromDate.getDueDate().toString());
	}
	@Test
	public void testCheckoutDate_checkoutDate_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals("Wed Sep 01 01:00:00 PDT 2021",checkoutDateFromDate.getCheckoutDate().toString());
	}
	@Test
	public void testCheckoutDate_chargeDays_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-08-01 01:00:00",
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(15,checkoutDateFromDate.getChargeDays());
		assertEquals(0, checkoutDateFromDate.getFreeDays());
	}
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14,checkoutDateFromDate.getChargeDays());
		assertEquals(1, checkoutDateFromDate.getFreeDays());
	}
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeTrue_LaborDay_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						true,
						true,
						true);
		assertNotNull(checkoutDateFromDate);
		assertEquals(15,checkoutDateFromDate.getChargeDays());
		assertEquals(0, checkoutDateFromDate.getFreeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysWeekendChargeFalse_chargeDaysHolidayChargeFalse_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						true,
						false,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(10,checkoutDateFromDate.getChargeDays());
		assertEquals(5, checkoutDateFromDate.getFreeDays());
	}
	
	@Test
	public void testCheckoutDate_allChargeDaysFalse_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						false,
						false,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(0,checkoutDateFromDate.getChargeDays());
		assertEquals(15, checkoutDateFromDate.getFreeDays());
	}
	
	@Test
	public void testCheckoutDate_weekDayChargeFalse_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						false,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(4,checkoutDateFromDate.getChargeDays());
		assertEquals(11, checkoutDateFromDate.getFreeDays());
	}
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnWeekend_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-07-01 01:00:00",
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14,checkoutDateFromDate.getChargeDays());
		assertEquals(1, checkoutDateFromDate.getFreeDays());
	}
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDayOnWorkDay_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2023-07-01 01:00:00",
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14,checkoutDateFromDate.getChargeDays());
		assertEquals(1, checkoutDateFromDate.getFreeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_LaborDay_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-09-01 01:00:00",
						15,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(14,checkoutDateFromDate.getChargeDays());
		assertEquals(1, checkoutDateFromDate.getFreeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-07-01 01:00:00",
						90,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(88,checkoutDateFromDate.getChargeDays());
		assertEquals(2, checkoutDateFromDate.getFreeDays());
	}
	
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeFalse_IndependenceDay_AND_LaborDay_MAX_VALUE_DAYS_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-07-01 01:00:00",
						Integer.MAX_VALUE,
						true,
						true,
						false);
		assertNotNull(checkoutDateFromDate);
		assertEquals(2135724425,checkoutDateFromDate.getChargeDays());
		assertEquals(11759222, checkoutDateFromDate.getFreeDays());
	}
	@Test
	public void testCheckoutDate_chargeDaysHolidayChargeTrue_IndependenceDay_AND_LaborDay_MAX_VALUE_DAYS_valid() {
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate("2021-07-01 01:00:00",
						Integer.MAX_VALUE,
						true,
						true,
						true);
		assertNotNull(checkoutDateFromDate);
		assertEquals(Integer.MAX_VALUE,checkoutDateFromDate.getChargeDays());
		assertEquals(0, checkoutDateFromDate.getFreeDays());
	}
	
}