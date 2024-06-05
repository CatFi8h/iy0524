package dev.yurchenko.iy0524.service.impl;

import dev.yurchenko.iy0524.service.dto.BillingDetailsDto;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.service.BillingDetailsService;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

@Component
public class BillingDetailsServiceImpl implements BillingDetailsService {
	
	@Override
	public BillingDetailsDto getBillingDetailsFromToolTypeAndCheckoutDate(Instant checkoutDate, int periodDaysTotal, ToolTypeEntity toolType) {
		
		LocalDate start = LocalDateTime.ofInstant(checkoutDate, ZoneOffset.UTC).toLocalDate();
		LocalDate end = start.plusDays(periodDaysTotal);
		
		long chargeDays = 0;
		if (toolType.getWeekdayCharge()) {
			long periodWithoutWeekends = daysInRangeWithoutWeekends(start, end);
			chargeDays += periodWithoutWeekends;
		}
		if (toolType.getWeekendCharge()) {
			long numberOfWeekends = periodDaysTotal - daysInRangeWithoutWeekends(start, end);
			chargeDays += numberOfWeekends;
		}
		if (!toolType.getHolidayCharge()) {
			if (toolType.getWeekdayCharge()) {
				//independenceDay count in range
				int independenceDay = getNumberOfHolidayMoveOnWorkdayIfOnWeekendInRange(start, end);
				//labor day count in range
				int laborDay = getNumberHolidayFirstDayOfWeekInMonthInRange(start, end);
				int holidaysCount = independenceDay + laborDay;
				chargeDays -= holidaysCount;
			}
		}
		
		return new BillingDetailsDto(periodDaysTotal, chargeDays,
				periodDaysTotal - chargeDays, Date.from(checkoutDate), Date.from(checkoutDate.plus(periodDaysTotal, ChronoUnit.DAYS)));
	}
	
	private long daysInRangeWithoutWeekends(LocalDate start, LocalDate end) {
		
		boolean startOnWeekend = false;
		boolean endOnWeekend = false;
		//if start day of week is on weekend we move pointer to next Monday
		if (start.getDayOfWeek().getValue() > 5) {
			start = start.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
			startOnWeekend = true;
		}
		//if end day of week is on weekend we move pointer to the previous Friday
		if (end.getDayOfWeek().getValue() > 5) {
			end = end.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
			endOnWeekend = true;
		}
		long weeks = ChronoUnit.WEEKS.between(start, end);
		//we need to add one day cause when we move pointer on the weekend we lose one day.
		int addValue = startOnWeekend || endOnWeekend ? 1 : 0;
		// case when we don't have pointer on weekends, range is smaller than week and dates are on different
		// weeks and start day of week index is bigger than end day of week index
		if (start.getDayOfWeek().getValue() > end.getDayOfWeek().getValue()
				    && end.getDayOfYear() - start.getDayOfYear() < 7
				    && !(startOnWeekend || endOnWeekend)) {
			return end.getDayOfWeek().getValue() + (5 - start.getDayOfWeek().getValue());
		}
		return (weeks * 5) + addValue + Math.abs(end.getDayOfWeek().getValue() - start.getDayOfWeek().getValue());
	}
//			Independence Day, July 4th - If falls on weekend, it is observed on the closest weekday (if Sat,
//			then Friday before, if Sunday, then Monday after)
	
	private int getNumberOfHolidayMoveOnWorkdayIfOnWeekendInRange(LocalDate start, LocalDate end) {
		int count = 0;
		if (end.getYear() - start.getYear() >= 1) {
			count = end.getYear() - start.getYear() - 1;
			LocalDate startLastYearDay = start.with(TemporalAdjusters.lastDayOfYear());
			LocalDate endFirstYearDay = end.with(TemporalAdjusters.firstDayOfYear());
			count = getCountHolidayInRangeMoveFromWeekendOnNearestWorkday(start, startLastYearDay, start.getYear(), count);
			count = getCountHolidayInRangeMoveFromWeekendOnNearestWorkday(endFirstYearDay, end, end.getYear(), count);
		} else {
			count = getCountHolidayInRangeMoveFromWeekendOnNearestWorkday(start, end, start.getYear(), count);
		}
		
		return count;
	}
	
	private int getCountHolidayInRangeMoveFromWeekendOnNearestWorkday(LocalDate start, LocalDate end, int year, int count) {
		LocalDate independenceDay = LocalDate.of(year, Month.JULY, 4);
		if (independenceDay.getDayOfWeek().getValue() > 5) {
			int dayOfWeek = independenceDay.getDayOfWeek().getValue();
			//if dayOfWeek == Sunday we make Monday holiday
			if (dayOfWeek == 7) {
				count = getCountIndependenceDayOnDateRange(start, end, independenceDay.plusDays(1), count);
			}
			//if dayOfWeek == Saturday we make Friday holiday
			if (dayOfWeek == 6) {
				count = getCountIndependenceDayOnDateRange(start, end, independenceDay.minusDays(1), count);
			}
		} else {
			count = getCountIndependenceDayOnDateRange(start, end, independenceDay, count);
		}
		return count;
	}
	
	private static int getCountIndependenceDayOnDateRange(LocalDate start, LocalDate end, LocalDate independenceDay, int count) {
		if (!end.isBefore(independenceDay) && !start.isAfter(independenceDay)) {
			count++;
		}
		return count;
	}
//		Labor Day - First Monday in September
	
	private int getNumberHolidayFirstDayOfWeekInMonthInRange(LocalDate start, LocalDate end) {
		int count = 0;
		if (end.getYear() - start.getYear() >= 1) {
			count = end.getYear() - start.getYear() - 1;
			LocalDate startLastYearDay = start.with(TemporalAdjusters.lastDayOfYear());
			LocalDate endFirstYearDay = end.with(TemporalAdjusters.firstDayOfYear());
			count = getCountOfHolidaysInDaysRangeWithFirstMondayInMonth(start, startLastYearDay, start.getYear(), count);
			count = getCountOfHolidaysInDaysRangeWithFirstMondayInMonth(endFirstYearDay, end, end.getYear(), count);
		} else if (end.getYear() - start.getYear() == 0) {
			count = getCountOfHolidaysInDaysRangeWithFirstMondayInMonth(start, end, start.getYear(), count);
		}
		return count;
	}
	
	private static int getCountOfHolidaysInDaysRangeWithFirstMondayInMonth(LocalDate start, LocalDate end, int year, int count) {
		LocalDate firstOfSeptember = LocalDate.of(year, Month.SEPTEMBER, 1);
		LocalDate laborDay = firstOfSeptember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
		if (!start.isAfter(laborDay) && !end.isBefore(laborDay)) {
			count++;
		}
		return count;
	}
}
