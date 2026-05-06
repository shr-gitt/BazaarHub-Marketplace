package com.bazaarhub.backend.feature.points.repository;

import com.bazaarhub.backend.feature.points.entity.Points;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointsRepository extends JpaRepository<Points, Long> {
    Optional<Points> findByUserId(Long userId);

}

