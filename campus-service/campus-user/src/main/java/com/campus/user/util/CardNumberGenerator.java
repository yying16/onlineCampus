package com.campus.user.util;

import java.util.Random;

public class CardNumberGenerator {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final int CARD_NUMBER_LENGTH = 16;
    private static final int CARD_CODE_LENGTH = 4;

    public static void main(String[] args) {
        String cardNumber = generateCardNumber();
        String cardCode = generateCardCode();
        String cardKey = generateCardKey();

        System.out.println("Card Number: " + cardNumber);
        System.out.println("Card Code: " + cardCode);
        System.out.println("Card Key: " + cardKey);
    }

    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();

        // Generate random alphabets
        Random random = new Random();
        for (int i = 0; i < CARD_NUMBER_LENGTH - CARD_CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }

        // Generate random numbers
        for (int i = 0; i < CARD_CODE_LENGTH; i++) {
            int index = random.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }

        return sb.toString();
    }

    public static String generateCardCode() {
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < CARD_CODE_LENGTH; i++) {
            int index = random.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }

        return sb.toString();
    }

    public static String generateCardKey() {
        // Combine card number and card code with a hyphen
        return generateCardNumber() + "-" + generateCardCode();
    }
}
