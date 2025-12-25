package com.example.recipeapp.data;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.recipeapp.api.Supabase;
import com.example.recipeapp.model.User;
import com.example.recipeapp.util.UtilityService;
import com.google.gson.Gson;
public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_ROLE = "role";
    private final SharedPreferences prefs;
    private final Gson gson;
    private final Context context;
    public AuthRepository(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }
    public void register(String username, String email, String password, AuthCallback callback) {
        String passwordHash = UtilityService.hashPassword(password);
        Supabase.registerUser(context, username, passwordHash, email,
                () -> {
                    // Успешная регистрация
                    saveUserData(0, email, username, "token_" + System.currentTimeMillis(), "user");
                    callback.onSuccess("Регистрация успешна");
                    Log.d(TAG, "User registered: " + email);
                },
                () -> {
                    // Ошибка регистрации
                    callback.onError("Ошибка регистрации. Возможно, email уже используется");
                    Log.e(TAG, "Registration failed: " + email);
                }
        );
    }
    public void login(String username, String password, AuthCallback callback) {
        String passwordHash = UtilityService.hashPassword(password);
        Supabase.loginUser(context, username, passwordHash,
                () -> {
                    // Успешный вход
                    saveUserData(0, username, username, "token_" + System.currentTimeMillis(), "user");
                    callback.onSuccess("Вход выполнен");
                    Log.d(TAG, "User logged in: " + username);
                },
                () -> {
                    // Ошибка входа
                    callback.onError("Неверные учетные данные");
                    Log.e(TAG, "Login failed: " + username);
                }
        );
    }
    public void sendPasswordReset(String email, AuthCallback callback) {
        callback.onSuccess("Письмо отправлено на почту");
    }
    public void logout(){
        prefs.edit().clear().apply();
        Log.d(TAG, "User logged out");
    }
    public boolean isLoggedIn() {
        return prefs.getInt(KEY_USER_ID, -1) != -1;
    }
    public User getCurrentUser() {
        return new User(
                getUserId(),
                getEmail(),
                getUsername(),
                getRole()
        );
    }
    private void saveUserData(int userId, String email, String username, String token, String role) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .putString(KEY_USERNAME, username)
                .putString(KEY_TOKEN, token)
                .putString(KEY_ROLE, role)
                .apply();
        Log.d(TAG, "User data saved: " + email);
    }
    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
    public String getRole() {
        return prefs.getString(KEY_ROLE, "user");
    }
    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}