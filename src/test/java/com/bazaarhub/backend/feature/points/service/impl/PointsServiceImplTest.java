package com.bazaarhub.backend.feature.points.service.impl;

import com.bazaarhub.backend.feature.points.entity.Points;
import com.bazaarhub.backend.feature.points.exception.PointsNotFoundException;
import com.bazaarhub.backend.feature.points.mapper.PointsMapper;
import com.bazaarhub.backend.feature.points.repository.PointsRepository;
import com.bazaarhub.backend.feature.points.resource.response.PointsResponseDto;
import com.bazaarhub.backend.feature.user.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointsServiceImplTest {

    @Mock
    private PointsRepository pointsRepository;

    @Mock
    private PointsMapper pointsMapper;

    @InjectMocks
    private PointsServiceImpl pointsService;

    private User user;
    private Points points;
    private PointsResponseDto pointsResponseDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        points = new Points();
        points.setUser(user);
        points.setPoints(0);

        pointsResponseDto = new PointsResponseDto(
                1L,
                1L,
                0,
                null,
                null
        );
    }

    @Test
    void getPointsByUserId_shouldReturnPoints_whenFound() {
        when(pointsRepository.findByUserId(1L)).thenReturn(Optional.of(points));
        when(pointsMapper.mapToPointsResponse(points)).thenReturn(pointsResponseDto);

        PointsResponseDto result = pointsService.getPointsByUserId(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getUserId());
        Assertions.assertEquals(0, result.getPoints());

        verify(pointsRepository, times(1)).findByUserId(1L);
        verify(pointsMapper, times(1)).mapToPointsResponse(points);
    }

    @Test
    void getPointsByUserId_shouldThrowException_whenNotFound() {
        when(pointsRepository.findByUserId(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(PointsNotFoundException.class,
                () -> pointsService.getPointsByUserId(99L));

        verify(pointsRepository, times(1)).findByUserId(99L);
        verify(pointsMapper, never()).mapToPointsResponse(any());
    }


    @Test
    void updatePoints_shouldEarnPoints_whenPointsRecordExists() {
        points.setPoints(20);

        when(pointsRepository.findByUserId(1L)).thenReturn(Optional.of(points));
        when(pointsRepository.save(any(Points.class))).thenReturn(points);

        pointsService.updatePoints(user, new BigDecimal("1000"));

        Assertions.assertEquals(30, points.getPoints());

        verify(pointsRepository, times(1)).findByUserId(1L);
        verify(pointsRepository, times(1)).save(any(Points.class));
    }

    @Test
    void updatePoints_shouldCreateNewPointsRecord_whenNoneExists() {
        when(pointsRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(pointsRepository.save(any(Points.class))).thenReturn(points);

        pointsService.updatePoints(user, new BigDecimal("500"));

        verify(pointsRepository, times(2)).save(any(Points.class));
        verify(pointsRepository, times(1)).findByUserId(1L);
    }

    @Test
    void updatePoints_shouldEarnZeroPoints_whenOrderAmountBelowHundred() {
        points.setPoints(10);

        when(pointsRepository.findByUserId(1L)).thenReturn(Optional.of(points));
        when(pointsRepository.save(any(Points.class))).thenReturn(points);


        pointsService.updatePoints(user, new BigDecimal("99"));

        Assertions.assertEquals(10, points.getPoints()); // unchanged

        verify(pointsRepository, times(1)).findByUserId(1L);
        verify(pointsRepository, times(1)).save(any(Points.class));
    }

    @Test
    void updatePoints_shouldAccumulatePoints_correctly() {
        points.setPoints(0);

        when(pointsRepository.findByUserId(1L)).thenReturn(Optional.of(points));
        when(pointsRepository.save(any(Points.class))).thenReturn(points);


        pointsService.updatePoints(user, new BigDecimal("3000"));

        Assertions.assertEquals(30, points.getPoints());

        verify(pointsRepository, times(1)).findByUserId(1L);
        verify(pointsRepository, times(1)).save(any(Points.class));
    }
}