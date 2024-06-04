package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.dto.ToolDto;
import dev.yurchenko.iy0524.entites.ToolEntity;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.repository.ToolRepository;
import dev.yurchenko.iy0524.service.dto.DateCheckout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ToolsService {
	
	private final ToolRepository toolRepository;
	private final DateCheckService dateCheckService;
	
	private List<ToolEntity> getAllToolsFromRepository() {
		return toolRepository.findAll();
	}
	
	private List<ToolDto> getAllToolsDto(List<ToolEntity> toolEntityList) {
		return toolEntityList.stream()
				       .map(e -> new ToolDto(e.getId(), e.getCode(), e.getBrand().getName(), e.getToolType().getName(), e.getToolType().getDailyCharge()))
				       .toList();
	}
	
	public List<ToolDto> getAllTools() {
		return getAllToolsDto(getAllToolsFromRepository());
	}
	
	public RentalAgreementResponse checkout(ToolRequest request) {
		Integer discountPercents = request.discount();
		Integer rentDays = request.days();
		
		validateRequestDay(rentDays);
		validateRequestDiscount(discountPercents);
		
		ToolEntity toolEntity = toolRepository.checkoutWith(request.code());
		
		if (toolEntity == null) {
			throw new IllegalArgumentException(String.format("Tool with Code :%s not found", request.code()));
		}
		ToolTypeEntity toolType = toolEntity.getToolType();
		DateCheckout checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(request.checkoutDate(),
						rentDays,
						toolType.getWeekdayCharge(),
						toolType.getWeekendCharge(),
						toolType.getHolidayCharge());
		
		BigDecimal preDiscountCharge = calculatePriceWithoutDiscount(toolType.getDailyCharge(), checkoutDateFromDate.getChargeDays());
		
		BigDecimal discountAmount = new BigDecimal(discountPercents).setScale(2, RoundingMode.HALF_UP).divide(new BigDecimal(100).setScale(2, RoundingMode.HALF_UP), RoundingMode.HALF_UP)
				                               .multiply(preDiscountCharge).setScale(2, RoundingMode.HALF_UP);
		
		return new RentalAgreementResponse(toolEntity.getCode(),
				toolEntity.getBrand().getName(),
				toolType.getName(),
				checkoutDateFromDate.getRentalDays(),
				checkoutDateFromDate.getCheckoutDate(),
				checkoutDateFromDate.getDueDate(),
				toolType.getDailyCharge(),
				checkoutDateFromDate.getChargeDays(),
				preDiscountCharge,
				discountPercents,
				discountAmount,
				preDiscountCharge.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP));
	}
	
	private BigDecimal calculatePriceWithoutDiscount(BigDecimal price, long days) {
		
		return price.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
		
	}
	
	
	private void validateRequestDiscount(Integer discount) throws IllegalArgumentException {
		if (discount == null || discount < 0 || discount > 100)
			throw new IllegalArgumentException("Wrong Input. Discount can not be out of range 0-100.");
	}
	
	private void validateRequestDay(Integer days) throws IllegalArgumentException {
		if (days == null || days < 1) throw new IllegalArgumentException("Wrong Input. Days can not be less then ONE.");
	}
}
