package com.CS360.stocksense.Utils;

import java.security.SecureRandom;

public class Utils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 16;
    private static final SecureRandom random = new SecureRandom();

    public static String generateDatabaseId() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Example usage
        System.out.println("Generated ID: " + generateDatabaseId());
    }
}