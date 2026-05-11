package com.bazaarhub.backend.feature.address.repository;

import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.shared.enums.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE a.street = :street AND a.municipality = :municipality")
    Optional<Address> findByStreetAndMunicipality(@Param("street") String street, @Param("municipality") Municipality municipality);
}