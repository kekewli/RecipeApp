package com.example.recipeapp.auth;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.api.Supabase;
import com.example.recipeapp.util.UtilityService;
import com.example.recipeapp.util.ValidationUtils;
public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private TextView loginLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        registerButton.setOnClickListener(v -> performRegister());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
    private void performRegister() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String usernameError = ValidationUtils.getUsernameError(username);
        if (usernameError != null) {
            usernameEditText.setError(usernameError);
            return;
        }
        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            emailEditText.setError(emailError);
            return;
        }
        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            passwordEditText.setError(passwordError);
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Пароли не совпадают");
            return;
        }
        registerButton.setEnabled(false);
        registerButton.setText("Регистрация...");
        String hashedPassword = UtilityService.hashPassword(password);
        Supabase.registerUser(this, username, hashedPassword, email,
                () -> {
                    registerButton.setEnabled(true);
                    registerButton.setText("Регистрация");
                    Toast.makeText(RegisterActivity.this, "Успешная регистрация!", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("email", email);
                    editor.apply();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                },
                () -> {
                    registerButton.setEnabled(true);
                    registerButton.setText("Регистрация");
                    Toast.makeText(RegisterActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
        );
    }
}