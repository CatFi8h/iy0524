package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.service.dto.BillingDetailsDto;
import dev.yurchenko.iy0524.entites.ToolTypeEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public interface BillingDetailsService {
	
	BillingDetailsDto getBillingDetailsFromToolTypeAndCheckoutDate(Instant checkoutDate, int periodDaysTotal, ToolTypeEntity toolType);
}
