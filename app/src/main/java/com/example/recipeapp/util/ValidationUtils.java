package com.example.recipeapp.util;
import android.util.Patterns;
public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null &&
                !email.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 20) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]+$");
    }
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
    public static String getEmailError(String email) {
        if (email == null || email.isEmpty()) {
            return "Email не может быть пустым";
        }
        if (!isValidEmail(email)) {
            return "Некорректный формат email";
        }
        return null;
    }
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Пароль не может быть пустым";
        }
        if (password.length() < 6) {
            return "Пароль должен быть минимум 6 символов";
        }
        return null;
    }
    public static String getUsernameError(String username) {
        if (username == null || username.isEmpty()) {
            return "Имя пользователя не может быть пустым";
        }
        if (username.length() < 3) {
            return "Имя должно быть минимум 3 символа";
        }
        if (username.length() > 50) {
            return "Имя не должно быть больше 50 символов";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "Только буквы, цифры и подчёркивание";
        }
        return null;
    }
}
