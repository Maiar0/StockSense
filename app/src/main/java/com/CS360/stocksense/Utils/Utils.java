package com.CS360.stocksense.Utils;

import java.security.SecureRandom;
/**
 * Utility class providing helper functions used throughout the StockSense application.
 *
 * <p>
 * Features:
 * - Generates a secure random database ID.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class Utils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 16;
    private static final SecureRandom random = new SecureRandom();
    /**
     * Generates a secure random database ID consisting of alphanumeric characters.
     *
     * @return A randomly generated database ID as a String.
     */
    public static String generateDatabaseId() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
