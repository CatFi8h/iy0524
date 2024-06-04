package dev.yurchenko.iy0524.controller.request;

import java.time.Instant;

public record ToolRequest(String code, Integer days, Integer discount, Instant checkoutDate) {
}
