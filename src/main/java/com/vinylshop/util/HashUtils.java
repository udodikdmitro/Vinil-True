package com.vinylshop.util;

import java.security.MessageDigest;

public final class HashUtils {
    private HashUtils() {}

    public static String hashSHA1(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(bytes);

            byte[] hashBytes = digest.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}