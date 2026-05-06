package com.bazaarhub.backend.feature.points.service;

import com.bazaarhub.backend.feature.points.resource.response.PointsResponseDto;
import com.bazaarhub.backend.feature.user.entity.User;

import java.math.BigDecimal;

public interface PointsService {

    PointsResponseDto getPointsByUserId(Long userId);

    void updateUserPoints(User user, BigDecimal totalAmount);
}

