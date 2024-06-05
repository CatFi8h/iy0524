package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.dto.DateCheckoutDto;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public interface DateCheckService {
	DateCheckoutDto getBillingDetailsFromToolTypeAndCheckoutDate(Instant date,
	                                                             int periodDaysTotal,
	                                                             Boolean weekdayCharge,
	                                                             Boolean weekendCharge,
	                                                             Boolean holidayCharge);
}
