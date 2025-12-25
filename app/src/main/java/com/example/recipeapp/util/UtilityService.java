package com.example.recipeapp.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class UtilityService {
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 не обнаружен", e);
        }
    }
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}

