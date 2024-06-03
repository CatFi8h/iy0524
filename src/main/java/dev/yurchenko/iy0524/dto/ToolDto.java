package dev.yurchenko.iy0524.dto;

import java.math.BigDecimal;

public record ToolDto(long id, String code, String brand, String type, BigDecimal price) {
}
