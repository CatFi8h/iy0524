package dev.yurchenko.iy0524.dto;

import java.util.Date;

public record DateCheckoutDto(long rentalDays, long chargeDays, long freeDays, Date checkoutDate, Date dueDate) {


}

