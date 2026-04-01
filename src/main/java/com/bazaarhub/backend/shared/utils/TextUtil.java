package com.bazaarhub.backend.shared.utils;

public class TextUtil {
    private TextUtil() {} // prevent instantiation

    // Capitalize First Letter Of Each Word In A Phrase
    public static String capitalizeEachWord(String text) {
        if (text == null || text.isBlank()) return text;

        text = text.trim().replaceAll("\\s+", " ");

        StringBuilder result = new StringBuilder();
        for (String word : text.split(" ")) {
            result.append(
                    Character.toUpperCase(word.charAt(0))
                            + word.substring(1).toLowerCase()
            ).append(" ");
        }
        return result.toString().trim();
    }

    // Capitalize First Letter Of Word
    public static String capitalizeFirstLetter(String text) {
        if (text == null || text.isBlank()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    //Email lowercase
    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
