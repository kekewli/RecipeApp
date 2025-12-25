package com.example.recipeapp.auth;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.api.Supabase;
import com.example.recipeapp.recipes.MainActivity;
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressDialog progressDialog;
    private boolean isActivityDestroyed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate вызван");
        isActivityDestroyed = false;
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);
        TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
        loginButton.setOnClickListener(v -> handleLogin());
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "handleLogin: username=" + username);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Вход в систему...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "Начало входа для пользователя: " + username);
        Supabase.loginUser(
                LoginActivity.this,
                username,
                password,
                () -> onLoginSuccess(),
                () -> onLoginFailure()
        );
    }
    private void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess вызван, isActivityDestroyed=" + isActivityDestroyed);
        if (isActivityDestroyed) {
            Log.w(TAG, "Activity уничтожена");
            return;
        }
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при закрытии диалога: " + e.getMessage());
        }
        try {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при переходе на MainActivity: " + e.getMessage());
        }
    }
    private void onLoginFailure() {
        Log.d(TAG, "onLoginFailure вызван, isActivityDestroyed=" + isActivityDestroyed);
        if (isActivityDestroyed) {
            Log.w(TAG, "Activity уже уничтожена");
            return;
        }
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при закрытии диалога: " + e.getMessage());
        }
        try {
            Toast.makeText(LoginActivity.this, "Ошибка входа. Проверьте учетные данные", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка показа Toast: " + e.getMessage());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume вызван");
        isActivityDestroyed = false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause вызван");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy вызван");
        isActivityDestroyed = true;
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при закрытии диалога в onDestroy: " + e.getMessage());
            }
        }
    }
}