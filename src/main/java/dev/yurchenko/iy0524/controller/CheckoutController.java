package dev.yurchenko.iy0524.controller;

import dev.yurchenko.iy0524.controller.request.ToolCheckoutRequestDto;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponseDto;
import dev.yurchenko.iy0524.exception.RequestValidationException;
import dev.yurchenko.iy0524.service.ToolEntityCheckoutService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RequiredArgsConstructor
@RestController
public class CheckoutController {
	
	private final ToolEntityCheckoutService toolEntityCheckoutService;
	
	@PostMapping("/checkout")
	public @ResponseBody RentalAgreementResponseDto getRentalAgreementForCheckoutTool(@RequestBody ToolCheckoutRequestDto request) {
		validateToolCheckoutRequest(request);
		return toolEntityCheckoutService.createRentalAgreementResponse(request);
	}
	
	private void validateToolCheckoutRequest(ToolCheckoutRequestDto toolCheckoutRequestDto) {
		if (toolCheckoutRequestDto == null) {
			throw new RequestValidationException("Invalid tool request");
		}
		String code = toolCheckoutRequestDto.code();
		int days = toolCheckoutRequestDto.days();
		Instant checkoutDate = toolCheckoutRequestDto.checkoutDate();
		int discount = toolCheckoutRequestDto.discount();
		if (StringUtils.isBlank(code) || code.length() != 4) {
			throw new RequestValidationException("Field 'code' should not be Blank and less nor bigger length 4");
		}
		if (days < 1) {
			throw new RequestValidationException("Field 'days' should not be less than 1.");
		}
		if (discount < 0 || discount > 100) {
			throw new RequestValidationException("Filed 'discount' should not be less than 0 and greater that 100");
		}
		if (checkoutDate == null) {
			throw new RequestValidationException("Filed 'checkoutDate' should not be empty");
		}
	}
}
