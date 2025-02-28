package com.digitalfutures.academy.spring_demo.utils;
import com.digitalfutures.academy.spring_demo.shared.FullName;

import java.util.HashMap;
import java.util.Map;

public class NameFormatter {

    private NameFormatter() {} // Private constructor to prevent instantiation

    public static Map<String, String> createNameMap(FullName fullName) {
        Map<String, String> nameInfo = new HashMap<>();
        nameInfo.put("first_name", fullName.getFirstName());

        if (fullName.getMiddleName() != null && !fullName.getMiddleName().isEmpty()) {
            nameInfo.put("middle_name", fullName.getMiddleName());
        }

        nameInfo.put("last_name", fullName.getLastName());

        return nameInfo;
    }
}
