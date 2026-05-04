package com.bazaarhub.backend.feature.payment.util;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Component
public class EsewaSignatureUtil {

    @Value("${esewa.secret.key}")
    private String esewaSecretKey;

    @PostConstruct
    public void validateSecretKey() {
        if (esewaSecretKey == null || esewaSecretKey.isBlank()) {
            throw new IllegalStateException("esewa.secret.key is not configured");
        }
    }

    public String generateSignature(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(esewaSecretKey.getBytes(), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            // HmacSHA256 is guaranteed by the JVM spec — this should never happen
            // if it does, it's a broken JVM environment, not a runtime error we can recover from
            throw new IllegalStateException("HmacSHA256 algorithm not available", e);
        } catch (InvalidKeyException e) {
            // means esewaSecretKey is empty or misconfigured — a config problem, not a payment problem
            throw new IllegalStateException("Invalid eSewa secret key — check esewa.secret.key in properties", e);
        }
    }

    public String buildSignedString(Map<String, String> data) {
        String[] fields = data.get("signed_field_names").split(",");
        return Arrays.stream(fields)
                .map(f -> f + "=" + data.getOrDefault(f, ""))
                .collect(Collectors.joining(","));
    }

    public boolean verifyCallback(Map<String, String> data) {
        String sigData = buildSignedString(data);
        String expected = generateSignature(sigData);
        return expected.equals(data.get("signature"));
    }
}