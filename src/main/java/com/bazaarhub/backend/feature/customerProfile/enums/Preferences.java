package com.bazaarhub.backend.feature.customerProfile.enums;

import lombok.Getter;

@Getter
public enum Preferences {

    ELECTRONICS(1), FASHION(2), GROCERIES(3), BEAUTY(4), HEALTH(5), FITNESS(6), SPORTS(7), BOOKS(8), STATIONERY(9), HOME_DECOR(10), FURNITURE(11), KITCHEN(12), AUTOMOTIVE(13), TOYS(14), BABY_PRODUCTS(15), PET_SUPPLIES(16), GAMING(17), MOBILE_ACCESSORIES(18), COMPUTERS(19), OTHERS(20);

    private final int id;

    Preferences(int id) {
        this.id = id;
    }

    public static Preferences fromId(int id) {
        for (Preferences p : values()) {
            if (p.id == id) return p;
        }
        throw new IllegalArgumentException("Unknown Preference ID: " + id);
    }


}


