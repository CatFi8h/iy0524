package dev.yurchenko.iy0524.controller;

import dev.yurchenko.iy0524.controller.request.ToolRequest;
import dev.yurchenko.iy0524.controller.response.RentalAgreementResponse;
import dev.yurchenko.iy0524.controller.response.ToolListResponse;
import dev.yurchenko.iy0524.dto.ToolDto;
import dev.yurchenko.iy0524.service.ToolsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CheckoutController {
	
	private final ToolsService toolsService;
	
	@GetMapping("/")
	public ToolListResponse getAllTools() {
	
		List<ToolDto> allToolsDto = toolsService.getAllTools();
		return new ToolListResponse(allToolsDto);
	}
	
	@PostMapping("/checkout")
	public @ResponseBody RentalAgreementResponse checkoutTool(@NonNull @RequestBody ToolRequest request) {
		return toolsService.checkout(request);
	}
}
