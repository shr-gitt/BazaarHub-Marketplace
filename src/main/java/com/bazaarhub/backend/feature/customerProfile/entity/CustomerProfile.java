package com.bazaarhub.backend.feature.customerProfile.entity;

import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.feature.customerProfile.helper.PreferencesConverter;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CustomerProfile extends BaseEntity {

    @Column(name = "imageUrl", nullable = false)
    private String profileImageUrl;

    @Column(name = "dob", nullable = false)
    private LocalDate dateOfBirth;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Convert(converter = PreferencesConverter.class)
    @Column(name = "preferences")
    private List<Integer> preferences = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
