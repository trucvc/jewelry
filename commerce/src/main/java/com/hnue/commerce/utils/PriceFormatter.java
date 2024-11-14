package com.hnue.commerce.utils;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class PriceFormatter {
    public static String formatPrice(double price) {
        DecimalFormat df = new DecimalFormat("#,###");
        return "VND " + df.format(price);
    }
}
