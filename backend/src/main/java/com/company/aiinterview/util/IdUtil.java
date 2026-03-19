package com.company.aiinterview.util;

import java.util.UUID;

public class IdUtil {
    private IdUtil() {
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }
}
