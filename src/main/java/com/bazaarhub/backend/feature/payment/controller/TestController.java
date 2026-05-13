package com.bazaarhub.backend.feature.payment.controller;

import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test/success")
    @LogExecutionTime
    public String success(@RequestParam(required = false) String orderId) {
        return "Payment successful for order: " + orderId;
    }

    @GetMapping("/test/failure")
    @LogExecutionTime
    public String failure() {
        return "Payment failed";
    }
}