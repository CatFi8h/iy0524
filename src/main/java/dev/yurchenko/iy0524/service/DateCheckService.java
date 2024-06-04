package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.dto.DateCheckoutDto;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

@Component
public class DateCheckService {

	public DateCheckoutDto getCheckoutDateFromDate(Instant date,
	                                               int periodDaysTotal,
	                                               Boolean weekdayCharge,
	                                               Boolean weekendCharge,
	                                               Boolean holidayCharge) {
		
		Date checkoutDate = Date.from(date);
		
		LocalDate start = checkoutDate.toInstant()
				                  .atZone(ZoneId.systemDefault())
				                  .toLocalDate();
		LocalDate end = start.plusDays(periodDaysTotal);
		
		long chargeDays = 0;
		if (weekdayCharge) {
			long periodWithoutWeekends = daysInRangeWithoutWeekends(start, end);
			chargeDays += periodWithoutWeekends;
		}
		if (weekendCharge) {
			long numberOfWeekends = periodDaysTotal - daysInRangeWithoutWeekends(start, end);
			chargeDays += numberOfWeekends;
		}
		int holidaysCount = 0;
		if (!holidayCharge) {
			if (weekdayCharge) {
				//independenceDay count in range
				int independenceDay = getNumberOfHolidayMoveOnWorkdayIfOnWeekendInRange(start, end);
				//labor day count in range
				int laborDay = getNumberHolidayFirstDayOfWeekInMonthInRange(start, end);
				holidaysCount = independenceDay + laborDay;
				chargeDays -= holidaysCount;
			}
		}
//		long periodWithoutWeekends = daysInRangeWithoutWeekends(start, end);
//		long numberOfWeekends = periodDaysTotal - daysInRangeWithoutWeekends(start, end);
////		long numberOfWeekends = getWeekendsInRange(start, end);
////		long numberOfWeekends = periodDaysTotal - periodWithoutWeekends;
//		//independenceDay count in range
//		int independenceDay = getNumberOfHolidayMoveOnWorkdayIfOnWeekendInRange(start, end);
//		//labor day count in range
//		int laborDay = getNumberHolidayFirstDayOfWeekInMonthInRange(start, end);
//		long freeDays = ((holidayCharge) ? 0 : (!weekdayCharge ? 0 : (laborDay + independenceDay)))
//				                + (weekdayCharge ? 0 : periodWithoutWeekends)
//				                + (weekendCharge ? 0 : numberOfWeekends);
		
		return new DateCheckoutDto(periodDaysTotal, chargeDays,
				periodDaysTotal - chargeDays, checkoutDate, Date.from(date.plus(periodDaysTotal, ChronoUnit.DAYS)));
	}
	
	private long daysInRangeWithoutWeekends(LocalDate start, LocalDate end) {
		
		boolean startOnWeekend = false;
		boolean endOnWeekend = false;
		if (start.getDayOfWeek().getValue() > 5) {
			start = start.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
			startOnWeekend = true;
		}
		if (end.getDayOfWeek().getValue() > 5) {
			end = end.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
			endOnWeekend = true;
		}
		long weeks = ChronoUnit.WEEKS.between(start, end);
		int addValue = startOnWeekend || endOnWeekend ? 1 : 0;
		
		return (weeks * 5) + addValue + (end.getDayOfWeek().getValue() - start.getDayOfWeek().getValue());
	}
	
	private long getWeekendsInRange(LocalDate start, LocalDate end) {
		
		boolean startOnWeekend = false;
		boolean endOnWeekend = false;
		LocalDate startDay = start;
		LocalDate endDay = end;
		if (start.getDayOfWeek().getValue() > 5) {
			start = start.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
			startOnWeekend = true;
		}
		if (end.getDayOfWeek().getValue() > 5) {
			end = end.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
			endOnWeekend = true;
		}
		long weeks = ChronoUnit.WEEKS.between(start, end);
		int addValue = 0;
		if (startOnWeekend) {
			addValue += start.getDayOfYear() - startDay.getDayOfYear();
		}
		if (endOnWeekend) {
			addValue += end.getDayOfYear() - endDay.getDayOfYear();
		}
		return (weeks * 2) + addValue;
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
	
	private Date getDateFromStringValue(String date) {
		DateFormat dform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dform.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Checkout date can not be parsed correctly");
		}
		
	}
	
	
}
