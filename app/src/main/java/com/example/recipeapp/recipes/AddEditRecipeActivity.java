package com.example.recipeapp.recipes;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.api.Supabase;
import com.example.recipeapp.model.Recipe;
import java.io.IOException;
import java.io.InputStream;
public class AddEditRecipeActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText ingredientsEditText;
    private Spinner categorySpinner;
    private ImageView recipeImageView;
    private Button addPhotoButton;
    private Button saveButton;
    private Button deleteButton;
    private Button addToFavoritesButton;
    private Button createRequestButton;
    private int recipeId = -1;
    private Uri selectedImageUri;
    private Recipe currentRecipe;
    private boolean isEditMode = false;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private int currentUserId;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);
        // Инициализация
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        recipeImageView = findViewById(R.id.recipeImageView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        addToFavoritesButton = findViewById(R.id.addToFavoritesButton);
        createRequestButton = findViewById(R.id.createRequestButton);
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);
        // Получение ID рецепта если это режим редактирования
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("RECIPE_ID")) {
            recipeId = intent.getIntExtra("RECIPE_ID", -1);
            isEditMode = true;
            loadRecipe();
        }
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                recipeImageView.setImageBitmap(bitmap);
                                if (inputStream != null) inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        addPhotoButton.setOnClickListener(v -> pickImage());
        saveButton.setOnClickListener(v -> saveRecipe());
        deleteButton.setOnClickListener(v -> deleteRecipe());
        addToFavoritesButton.setOnClickListener(v -> addToFavorites());
        createRequestButton.setOnClickListener(v -> createRequest());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("AddEditRecipeActivity", "Остановлено");
    }
    // Загрузка рецепта
    private void loadRecipe() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка рецепта...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Supabase.getRecipeDetails(this, recipeId,
                recipe -> {
                    progressDialog.dismiss();
                    currentRecipe = recipe;
                    displayRecipe(recipe);
                    saveButton.setVisibility(android.view.View.GONE);
                    addPhotoButton.setVisibility(android.view.View.GONE);
                    deleteButton.setVisibility(android.view.View.GONE);
                    addToFavoritesButton.setVisibility(android.view.View.VISIBLE);
                    titleEditText.setEnabled(false);
                    descriptionEditText.setEnabled(false);
                    ingredientsEditText.setEnabled(false);
                    categorySpinner.setEnabled(false);
                },
                () -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Ошибка загрузки рецепта", Toast.LENGTH_SHORT).show();
                }
        );
    }
    private void displayRecipe(Recipe recipe) {
        titleEditText.setText(recipe.getTitle());
        descriptionEditText.setText(recipe.getDescription());
        ingredientsEditText.setText(recipe.getIngredients());
        // Загрузить изображение
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .into(recipeImageView);
        }
    }
    private void addToFavorites() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Необходимо авторизоваться", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Добавление в избранное...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Supabase.addRecipeToFavorites(this, currentUserId, recipeId,
                () -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Добавлено в избранное!", Toast.LENGTH_SHORT).show();
                },
                () -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                }
        );
    }
    private void createRequest() {
        if (currentUserId == -1) {
            return;
        }
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String ingredients = ingredientsEditText.getText().toString().trim();
        int categoryId = 1;
        if (title.isEmpty() || description.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Отправка запроса...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String imageUrl = currentRecipe != null ? currentRecipe.getImageUrl() : "";
        Supabase.createUserRequest(this, currentUserId, title, description,
                ingredients, categoryId, imageUrl,
                () -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Запрос отправлен!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                () -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                }
        );
    }
    private void saveRecipe() {
        Toast.makeText(this, "Редактирование недоступно", Toast.LENGTH_SHORT).show();
    }
    private void deleteRecipe() {
        Toast.makeText(this, "Удаление недоступно", Toast.LENGTH_SHORT).show();
    }
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}