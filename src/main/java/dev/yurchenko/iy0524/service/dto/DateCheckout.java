package dev.yurchenko.iy0524.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateCheckout {
	private long rentalDays;
	private long chargeDays;
	private long freeDays;
	private Date checkoutDate;
	private Date dueDate;
}
