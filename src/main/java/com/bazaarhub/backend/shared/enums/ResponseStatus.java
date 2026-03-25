package com.bazaarhub.backend.shared.enums;

public enum ResponseStatus {
    SUCCESS("0"),
    ERROR("1"),
    FAILURE("2");

    public final String value;

    private ResponseStatus(String value) {
        this.value = value;
    }
}
