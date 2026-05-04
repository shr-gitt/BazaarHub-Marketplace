package com.bazaarhub.backend.feature.payment.service.impl;

import com.bazaarhub.backend.feature.payment.exception.EsewaVerificationException;
import com.bazaarhub.backend.feature.payment.util.EsewaSignatureUtil;
import com.bazaarhub.backend.feature.payment.service.EsewaService;
import com.bazaarhub.backend.feature.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class EsewaServiceImpl implements EsewaService {
    private final ObjectMapper objectMapper;
    private final EsewaSignatureUtil esewaSignatureUtil;
    private final PaymentService paymentService;

    @Value("${frontend.failure.url}")
    private String frontendFailureUrl;

    @Override
    public String processEsewaSuccess (String encodedData) {
        log.info("Processing successful payment through eSewa.");

        try {
            String decoded = new String(Base64.getDecoder().decode(encodedData));
            Map<String, String> data = objectMapper.readValue(decoded, Map.class);

            if (!esewaSignatureUtil.verifyCallback(data)) {
                log.error("eSewa signature mismatch — possible tampered callback");
                throw new EsewaVerificationException("Invalid signature");
            }

            String pid = data.get("transaction_uuid");
            String refId = data.get("transaction_code");
            String amt = data.get("total_amount");

            return paymentService.verifyPayment(pid, refId, amt);
        }
        catch (EsewaVerificationException e) {
            log.error("eSewa Verification error.");
            return frontendFailureUrl;
        }
        catch (Exception e) {
            log.error("Failed to parse eSewa callback", e);
            return frontendFailureUrl;
        }
    }

    @Override
    public String processEsewaFailure (String encodedData){
        log.info("Processing failure payment through eSewa.");

        try {
            String decoded = new String(Base64.getDecoder().decode(encodedData));
            Map<String, String> data = objectMapper.readValue(decoded, Map.class);

            if (!esewaSignatureUtil.verifyCallback(data)) {
                log.warn("Invalid failure callback signature");
                return frontendFailureUrl;
            }

            paymentService.markPaymentFailed(data.get("transaction_uuid"));
        } catch (JsonProcessingException e) {
            log.error("Invalid callback data", e);
        } catch (Exception e) {
            log.error("eSewa failure callback processing failed", e);
        }

        return frontendFailureUrl;
    }
}
