package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.dto.ToolDto;
import dev.yurchenko.iy0524.entites.ToolEntity;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import dev.yurchenko.iy0524.repository.ToolRepository;
import dev.yurchenko.iy0524.dto.DateCheckoutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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
	
	public RentalAgreementResponse createRentalAgreementResponse(ToolRequest request) {
		Integer discountPercents = request.discount();
		Integer rentDays = request.days();
		
		validateRequestDay(rentDays);
		validateRequestDiscount(discountPercents);
		
		Optional<ToolEntity> toolEntity = toolRepository.getToolWithDetailsByCode(request.code());
		
		if (toolEntity.isEmpty()) {
			throw new IllegalArgumentException(String.format("Tool with Code :%s not found", request.code()));
		}
		ToolTypeEntity toolType = toolEntity.get().getToolType();
		DateCheckoutDto checkoutDateFromDate =
				dateCheckService.getCheckoutDateFromDate(request.checkoutDate(),
						rentDays,
						toolType.getWeekdayCharge(),
						toolType.getWeekendCharge(),
						toolType.getHolidayCharge());
		
		BigDecimal preDiscountCharge = getPriceWithoutDiscount(toolType.getDailyCharge(), checkoutDateFromDate.chargeDays());
		BigDecimal discountAmount = getDiscountAmount(discountPercents, preDiscountCharge);
		BigDecimal finalCharge = preDiscountCharge.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
		
		return new RentalAgreementResponse(toolEntity.get().getCode(),
				toolEntity.get().getBrand().getName(),
				toolType.getName(),
				checkoutDateFromDate.rentalDays(),
				checkoutDateFromDate.checkoutDate(),
				checkoutDateFromDate.dueDate(),
				toolType.getDailyCharge(),
				checkoutDateFromDate.chargeDays(),
				preDiscountCharge,
				discountPercents,
				discountAmount,
				finalCharge);
	}
	
	private static BigDecimal getDiscountAmount(Integer discountPercents, BigDecimal preDiscountCharge) {
		BigDecimal percentAsDecimal = new BigDecimal(discountPercents).divide(new BigDecimal(100),2,  RoundingMode.HALF_UP);
		return percentAsDecimal.multiply(preDiscountCharge).setScale(2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal getPriceWithoutDiscount(BigDecimal price, long days) {
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