package com.bazaarhub.backend.feature.points.resource.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PointsResponseDto implements Serializable {

    private Long id;
    private Long userId;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}


