package com.bazaarhub.backend.feature.payment.controller;

import com.bazaarhub.backend.feature.payment.exception.EsewaVerificationException;
import com.bazaarhub.backend.feature.payment.service.EsewaService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/payment/esewa")
@RequiredArgsConstructor
public class EsewaCallbackController {
    private final EsewaService esewaService;

    @Value("${frontend.failure.url}")
    private String frontendFailureUrl;

    @GetMapping("/success")
    @LogExecutionTime
    public RedirectView handleSuccess(@RequestParam("data") String encodedData) {
        return new RedirectView(esewaService.processEsewaSuccess(encodedData));
    }

    @GetMapping("/failure")
    @LogExecutionTime
    public RedirectView handleFailure(@RequestParam("data") String encodedData) {
        return new RedirectView(esewaService.processEsewaFailure(encodedData));
    }
}