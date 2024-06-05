package dev.yurchenko.iy0524.controller.request;

import dev.yurchenko.iy0524.exception.RequestValidationException;
import io.micrometer.common.util.StringUtils;

import java.time.Instant;

public record ToolRequest(String code, int days, int discount, Instant checkoutDate) {
	public ToolRequest {
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
