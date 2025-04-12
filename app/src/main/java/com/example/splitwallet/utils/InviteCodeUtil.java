package com.example.splitwallet.utils;

public class InviteCodeUtil {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length(); // 36
    private static final int CODE_LENGTH = 6;
    private static final long OBFUSCATION_KEY = 0x7F4A9C13L; // XOR-ключ (можно любой фиксированный)
    public static String encode(Long number) {

        long obfuscated = number ^ OBFUSCATION_KEY;

        StringBuilder sb = new StringBuilder();
        while (obfuscated > 0) {
            int index = (int) (obfuscated % BASE);
            sb.append(ALPHABET.charAt(index));
            obfuscated /= BASE;
        }

        // Заполненяем слева '0', если длина меньше 6
        while (sb.length() < CODE_LENGTH) {
            sb.append('0');
        }

        return sb.reverse().toString(); // Переворачиваем, чтобы старшие разряды шли слева
    }

    public static Long decode(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            throw new IllegalArgumentException();
        }

        code = code.toUpperCase();

        long obfuscated = 0;
        for (int i = 0; i < code.length(); i++) {
            int index = ALPHABET.indexOf(code.charAt(i));
            if (index == -1) {
                throw new IllegalArgumentException();
            }
            obfuscated = obfuscated * BASE + index;
        }

        return obfuscated ^ OBFUSCATION_KEY;
    }

}
