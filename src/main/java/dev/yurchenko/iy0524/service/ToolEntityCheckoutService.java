package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolCheckoutRequestDto;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ToolEntityCheckoutService {
	
	RentalAgreementResponseDto createRentalAgreementResponse(ToolCheckoutRequestDto request);
}
