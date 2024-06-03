package dev.yurchenko.iy0524.controller.response;

import java.math.BigDecimal;
import java.util.Date;

public record RentalAgreementResponse(
		String code,
		String brand,
		String type,
		long days,
		Date date,
		Date dueDate,
		BigDecimal dailyCharge,
		long daysCharge,
		BigDecimal preDiscountCharge,
		int discountPercent,
		BigDecimal discountAmount,
		BigDecimal finalCharge
) {
}
