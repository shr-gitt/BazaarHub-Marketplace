package com.bazaarhub.backend.feature.payment.service;

public interface EsewaService {
    String processEsewaSuccess(String encodedData);

    String processEsewaFailure(String encodedData) ;
}
