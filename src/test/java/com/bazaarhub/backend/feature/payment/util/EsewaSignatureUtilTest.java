package com.bazaarhub.backend.feature.payment.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EsewaSignatureUtilTest {
    private EsewaSignatureUtil esewaSignatureUtil;

    @BeforeEach
    void setUp() {
        esewaSignatureUtil = new EsewaSignatureUtil();
        ReflectionTestUtils.setField(esewaSignatureUtil, "esewaSecretKey", "8gBm/:&EnhH.1/q");
    }

    @Test
    void generateSignature_shouldReturnConsistentResult() {
        String data = "total_amount=100.0,transaction_uuid=ORD-1-123,product_code=EPAYTEST";
        String sig1 = esewaSignatureUtil.generateSignature(data);
        String sig2 = esewaSignatureUtil.generateSignature(data);
        assertEquals(sig1, sig2);
    }

    @Test
    void generateSignature_differentInputShouldProduceDifferentSignature() {
        String sig1 = esewaSignatureUtil.generateSignature("total_amount=100.0,transaction_uuid=ORD-1-123,product_code=EPAYTEST");
        String sig2 = esewaSignatureUtil.generateSignature("total_amount=200.0,transaction_uuid=ORD-1-123,product_code=EPAYTEST");
        assertNotEquals(sig1, sig2);
    }

    @Test
    void verifyCallback_shouldReturnTrueForValidSignature() {
        Map<String, String> data = new HashMap<>();
        data.put("total_amount", "100.0");
        data.put("transaction_uuid", "ORD-1-123");
        data.put("product_code", "EPAYTEST");
        data.put("signed_field_names", "total_amount,transaction_uuid,product_code");

        String correctSig = esewaSignatureUtil.generateSignature(
                "total_amount=100.0,transaction_uuid=ORD-1-123,product_code=EPAYTEST"
        );
        data.put("signature", correctSig);

        assertTrue(esewaSignatureUtil.verifyCallback(data));
    }

    @Test
    void verifyCallback_shouldReturnFalseForTamperedAmount() {
        Map<String, String> data = new HashMap<>();
        data.put("total_amount", "1.0"); // tampered
        data.put("transaction_uuid", "ORD-1-123");
        data.put("product_code", "EPAYTEST");
        data.put("signed_field_names", "total_amount,transaction_uuid,product_code");
        data.put("signature", "fakesignature");

        assertFalse(esewaSignatureUtil.verifyCallback(data));
    }
}