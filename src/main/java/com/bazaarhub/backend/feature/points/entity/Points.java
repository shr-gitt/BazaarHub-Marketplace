package com.bazaarhub.backend.feature.points.entity;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "points")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Points extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "points", nullable = false)
    private Integer points = 0;
}