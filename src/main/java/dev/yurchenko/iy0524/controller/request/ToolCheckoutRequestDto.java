package dev.yurchenko.iy0524.controller.request;

import java.time.Instant;

public record ToolCheckoutRequestDto(String code, int days, int discount, Instant checkoutDate) {
}
