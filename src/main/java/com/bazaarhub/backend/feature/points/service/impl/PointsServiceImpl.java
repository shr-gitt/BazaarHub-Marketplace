package com.bazaarhub.backend.feature.points.service.impl;

import com.bazaarhub.backend.feature.points.entity.Points;
import com.bazaarhub.backend.feature.points.exception.PointsNotFoundException;
import com.bazaarhub.backend.feature.points.mapper.PointsMapper;
import com.bazaarhub.backend.feature.points.repository.PointsRepository;
import com.bazaarhub.backend.feature.points.resource.response.PointsResponseDto;
import com.bazaarhub.backend.feature.points.service.PointsService;
import com.bazaarhub.backend.feature.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final PointsRepository pointsRepository;
    private final PointsMapper pointsMapper;

    @Value("${points.rate}")
    private BigDecimal pointsRate;

    @Override
    public PointsResponseDto getPointsByUserId(Long userId) {
        log.info("Fetching points userId={}", userId);

        Points points = pointsRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Points not found userId={}", userId);
                    return new PointsNotFoundException("Points not found for user id");
                });

        return pointsMapper.mapToPointsResponse(points);
    }

    @Override
    public void updateUserPoints(User user, BigDecimal totalAmount) {

        Points points = pointsRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Points newPoints = new Points();
                    newPoints.setUser(user);
                    newPoints.setPoints(0);
                    return pointsRepository.save(newPoints);
                });


        int earned = totalAmount
                .multiply(pointsRate)
                .setScale(0, RoundingMode.DOWN)
                .intValue();

        points.setPoints(points.getPoints() + earned);
        pointsRepository.save(points);
        log.info("Points earned userId={}, thisOrderEarned={}, newBalance={}",
                user.getId(), earned, points.getPoints());
    }
}
