package com.movieticketapp.utils;

import java.text.DecimalFormat;

public final class PriceUtils {
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");

    private PriceUtils() {
    }

    public static String formatPrice(double price) {
        return PRICE_FORMAT.format(price);
    }
}
