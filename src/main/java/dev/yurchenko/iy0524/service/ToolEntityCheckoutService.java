package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.dto.ToolDto;
import dev.yurchenko.iy0524.entites.ToolEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ToolEntityCheckoutService {
	
	@Deprecated
	List<ToolDto> getAllTools();
	
	RentalAgreementResponse createRentalAgreementResponse(ToolRequest request);
}
