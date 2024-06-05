package dev.yurchenko.iy0524.controller.response;

public record RentalAgreementResponseDto(
		String code,
		String brand,
		String type,
		long days,
		String date,
		String dueDate,
		String dailyCharge,
		long daysCharge,
		String preDiscountCharge,
		String discountPercent,
		String discountAmount,
		String finalCharge
) {
}
