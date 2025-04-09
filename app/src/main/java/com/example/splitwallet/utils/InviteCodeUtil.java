package com.example.splitwallet.utils;

public class InviteCodeUtil {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public static String encode(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(ALPHABET.charAt((int)(num % BASE)));
            num /= BASE;
        }
        while (sb.length() < 6) sb.append('0'); // padding до 6 символов
        return sb.reverse().toString();
    }

    public static long decode(String code) {
        long num = 0;
        for (int i = 0; i < code.length(); i++) {
            num = num * BASE + ALPHABET.indexOf(code.charAt(i));
        }
        return num;
    }
}
