package com.bazaarhub.backend.feature.points.mapper;

import com.bazaarhub.backend.feature.points.entity.Points;
import com.bazaarhub.backend.feature.points.resource.response.PointsResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PointsMapper {

    public PointsResponseDto mapToPointsResponse(Points points) {
        return new PointsResponseDto(
                points.getId(),
                points.getUser().getId(),
                points.getPoints(),
                points.getCreatedAt(),
                points.getModifiedAt()
        );
    }
}

