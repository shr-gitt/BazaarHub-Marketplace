package com.bazaarhub.backend.feature.address.repository;

import com.bazaarhub.backend.feature.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AddressRepository extends JpaRepository<Address, Long> {
}