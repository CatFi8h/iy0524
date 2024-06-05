package dev.yurchenko.iy0524.service.impl;

import dev.yurchenko.iy0524.controller.request.ToolCheckoutRequestDto;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponseDto;
import dev.yurchenko.iy0524.entites.ToolEntity;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.exception.NoToolEntityFoundException;
import dev.yurchenko.iy0524.repository.ToolRepository;
import dev.yurchenko.iy0524.service.BillingDetailsService;
import dev.yurchenko.iy0524.service.ToolEntityCheckoutService;
import dev.yurchenko.iy0524.service.dto.BillingDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ToolsEntityCheckoutServiceImpl implements ToolEntityCheckoutService {
	
	private final ToolRepository toolRepository;
	private final BillingDetailsService billingDetailsService;
	
	
	@Override
	public RentalAgreementResponseDto createRentalAgreementResponse(ToolCheckoutRequestDto request) {
		int discountPercents = request.discount();
		int rentDays = request.days();
		
		Optional<ToolEntity> toolEntity = toolRepository.getToolWithDetailsByCode(request.code());
		
		if (toolEntity.isEmpty()) {
			throw new NoToolEntityFoundException(String.format("Tool with Code :%s not found", request.code()));
		}
		ToolTypeEntity toolType = toolEntity.get().getToolType();
		BillingDetailsDto checkoutDateFromDate =
				billingDetailsService.getBillingDetailsFromToolTypeAndCheckoutDate(request.checkoutDate(),
						rentDays,
						toolType);
		
		BigDecimal preDiscountCharge = getPriceWithoutDiscount(toolType.getDailyCharge(), checkoutDateFromDate.chargeDays());
		BigDecimal discountAmount = getDiscountAmount(discountPercents, preDiscountCharge);
		BigDecimal finalCharge = preDiscountCharge.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		
		return new RentalAgreementResponseDto(toolEntity.get().getCode(),
				toolEntity.get().getBrand().getName(),
				toolType.getName(),
				checkoutDateFromDate.rentalDays(),
				dateFormat.format(checkoutDateFromDate.checkoutDate()),
				dateFormat.format(checkoutDateFromDate.dueDate()),
				getStringCurrency(toolType.getDailyCharge()),
				checkoutDateFromDate.chargeDays(),
				getStringCurrency(preDiscountCharge),
				discountPercents + "%",
				getStringCurrency(discountAmount),
				getStringCurrency(finalCharge));
	}
	
	private String getStringCurrency(BigDecimal amount) {
		DecimalFormat df = new DecimalFormat("$#,##0.00");
		return df.format(amount);
	}
	
	private static BigDecimal getDiscountAmount(Integer discountPercents, BigDecimal preDiscountCharge) {
		BigDecimal percentAsDecimal = new BigDecimal(discountPercents).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		return percentAsDecimal.multiply(preDiscountCharge).setScale(2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal getPriceWithoutDiscount(BigDecimal price, long days) {
		return price.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
	}
}