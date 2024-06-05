package dev.yurchenko.iy0524.service.dto;

import java.util.Date;

public record BillingDetailsDto(long rentalDays, long chargeDays, long freeDays, Date checkoutDate, Date dueDate) {


}

