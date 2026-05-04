package com.bazaarhub.backend.feature.payment.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Profile("dev")
@RestController
public class TestController {
    @GetMapping("/test/success")
    public String success(@RequestParam(required = false) String orderId) {
        return "Payment successful for order: " + orderId;
    }

    @GetMapping("/test/failure")
    public String failure() {
        return "Payment failed";
    }
}