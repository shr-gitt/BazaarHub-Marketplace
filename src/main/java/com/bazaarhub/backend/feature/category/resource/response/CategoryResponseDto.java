package com.bazaarhub.backend.feature.category.resource.response;

import com.bazaarhub.backend.feature.category.enums.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto implements Serializable {

    private Long id;
    private Long version;
    private String name;
    private String description;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
