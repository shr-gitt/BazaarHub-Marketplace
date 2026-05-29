package com.bazaarhub.backend.feature.payment.controller;

import com.bazaarhub.backend.feature.payment.service.EsewaService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/payment/esewa")
@RequiredArgsConstructor
public class EsewaCallbackController {
    private final EsewaService esewaService;

    @GetMapping("/success")
    @LogExecutionTime
    public RedirectView handleSuccess(@RequestParam("data") String encodedData) {
        return new RedirectView(esewaService.processEsewaSuccess(encodedData));
    }

    @GetMapping("/failure")
    @LogExecutionTime
    public RedirectView handleFailure(@RequestParam(value = "data", required = false) String encodedData) {
        return new RedirectView(esewaService.processEsewaFailure(encodedData));
    }
}