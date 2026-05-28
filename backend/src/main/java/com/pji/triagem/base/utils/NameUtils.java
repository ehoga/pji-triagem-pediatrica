package com.pji.triagem.base.utils;

public class NameUtils {

    private NameUtils() {
    }

    public static String abbreviateName(String name, int maxLength) {
        String[] parts = name.split(" ");

        if (name.length() <= maxLength) {
            return name;
        }

        String firstName = parts[0];
        String lastName = parts[parts.length - 1];

        for (int i = 1; i < parts.length - 1; i++) {
            parts[i] = parts[i].charAt(0) + ".";
            String abbreviatedName = String.join(" ", parts);

            if (abbreviatedName.length() <= maxLength) {
                return abbreviatedName;
            }
        }

        for (int i = 1; i < parts.length - 1; i++) {
            for (int j = i + 1; j < parts.length - 1; j++) {
                parts[i] = parts[i].charAt(0) + ".";
                parts[j] = parts[j].charAt(0) + ".";
                String abbreviatedName = String.join(" ", parts);

                if (abbreviatedName.length() <= maxLength) {
                    return abbreviatedName;
                }
            }
        }

        StringBuilder abbreviatedName = new StringBuilder(firstName);
        int currentLength = firstName.length() + lastName.length() + 1;

        for (int i = 1; i < parts.length - 1; i++) {
            if (currentLength + 2 <= maxLength) { // 2 para "N."
                abbreviatedName.append(" ").append(parts[i].charAt(0)).append(".");
                currentLength += 3;
            } else {
                break;
            }
        }

        if (currentLength + lastName.length() + 1 <= maxLength) {
            abbreviatedName.append(" ").append(lastName);
        }

        return abbreviatedName.toString();
    }
}
