package com.bazaarhub.backend.feature.order.resources.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto implements Serializable {

    @NotBlank(message = "Shipping address is required.")
    private String shippingAddress;

    @NotBlank(message = "Contact number is required.")
    private String contactNumber;

    private String remark;

}
