package com.example.recipeapp.auth;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.recipeapp.R;
import com.example.recipeapp.api.Supabase;
import com.example.recipeapp.util.ValidationUtils;
public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String emailError = ValidationUtils.getEmailError(email);
            if (emailError != null) {
                emailEditText.setError(emailError);
                return;
            }
            resetButton.setEnabled(false);
            resetButton.setText("Отправка...");
            Supabase.createPasswordResetToken(this, email,
                    () -> {
                        resetButton.setEnabled(true);
                        resetButton.setText("Восстановить пароль");
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Письмо отправлено на почту", Toast.LENGTH_LONG).show();
                        finish();
                    },
                    () -> {
                        resetButton.setEnabled(true);
                        resetButton.setText("Восстановить пароль");
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Ошибка отправки письма", Toast.LENGTH_SHORT).show();
                    }
            );
        });
    }
}