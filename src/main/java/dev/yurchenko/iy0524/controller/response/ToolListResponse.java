package dev.yurchenko.iy0524.controller.response;


import dev.yurchenko.iy0524.dto.ToolDto;

import java.util.List;

public record ToolListResponse(List<ToolDto> toolDtoList) {
}
