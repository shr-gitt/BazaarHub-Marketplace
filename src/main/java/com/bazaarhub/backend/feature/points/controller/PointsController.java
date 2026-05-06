package com.bazaarhub.backend.feature.points.controller;

import com.bazaarhub.backend.feature.points.resource.response.PointsResponseDto;
import com.bazaarhub.backend.feature.points.service.PointsService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;
    private final AuthUtil authUtil;

    @GetMapping("/points")
    @LogExecutionTime
    public ApiResponseDto<PointsResponseDto> getMyPoints() {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Points fetched successfully.",
                pointsService.getPointsByUserId(authUtil.getCurrentUserId()));
    }
}