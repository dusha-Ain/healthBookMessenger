package com.rent.healthbookmessenger.web;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {


    private static int defaultPageSize;
    private static int defaultPageNumber;

    @Value("${default.page.size}")
    private int defaultPageSizeNonStatic;

    @Value("${default.page.number}")
    private int defaultPageNumberNonStatic;

    @PostConstruct
    private void init() {
        defaultPageSize = defaultPageSizeNonStatic;
        defaultPageNumber = defaultPageNumberNonStatic;
    }

    public static int getDefaultPageSize() {
        return defaultPageSize;
    }

    public static int getDefaultPageNumber() {
        return defaultPageNumber;
    }
}
