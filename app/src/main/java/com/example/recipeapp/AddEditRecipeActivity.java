package com.example.recipeapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class AddEditRecipeActivity extends AppCompatActivity {
    private RecipeDatabaseHelper dbHelper;
    private EditText titleEditText, descriptionEditText, ingredientsEditText;
    private Spinner categorySpinner;
    private ImageView recipeImageView;
    private int recipeId = -1;
    private static final int CAMERA_REQUEST = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //открытие галлереи
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);

        dbHelper = new RecipeDatabaseHelper(this);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        recipeImageView = findViewById(R.id.recipeImageView);
        Button addPhotoButton = findViewById(R.id.addPhotoButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        if (recipeId != -1) {
            loadRecipe();
            deleteButton.setVisibility(View.VISIBLE);
        }

        saveButton.setOnClickListener(v -> saveRecipe());
        deleteButton.setOnClickListener(v -> deleteRecipe());
        addPhotoButton.setOnClickListener(v -> openGallery());

        recipeImageView.setOnClickListener(v -> {
            if (recipeImageView.getDrawable() instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) recipeImageView.getDrawable()).getBitmap();
                showImageFullScreen(bitmap);
            } else {
                Toast.makeText(this, "Нет изображения!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private final ActivityResultLauncher<String> galleryLauncher = //открытие галлереи
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    String mimeType = getMimeType(this, result);
                    if (mimeType == null || (!mimeType.startsWith("image/") || mimeType.equals("image/gif"))) {
                        Toast.makeText(this, "Неподдерживаемый формат. Выберите JPG или PNG!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Bitmap compressedBitmap = getCompressedBitmapFromUri(result, 850, 850);
                        recipeImageView.setImageBitmap(compressedBitmap);
                    } catch (IOException e) {
                        //noinspection CallToPrintStackTrace
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка обработки изображения!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }
    private String getMimeType(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.getType(uri);
    }
    private void showImageFullScreen(Bitmap image) { //увеличение изображения
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        ImageView fullScreenImageView = dialog.findViewById(R.id.fullScreenImageView);
        fullScreenImageView.setImageBitmap(image);

        fullScreenImageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void deleteRecipe() { //удаление рецепта
        if (dbHelper.deleteRecipe(recipeId)) {
            Toast.makeText(this, "Рецепт удалён", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //загрузка изображения
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                recipeImageView.setImageBitmap(photo);
            }
        }
    }
    private Bitmap getCompressedBitmapFromUri(Uri uri, int reqWidth, int reqHeight) throws IOException { //сжатие изображения
        InputStream input = getContentResolver().openInputStream(uri);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        assert input != null;
        input.close();

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // коэффициент сжатия
        options.inJustDecodeBounds = false;

        input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        assert input != null;
        input.close();

        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) { //сжатие
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void saveRecipe() { //сохранение рецепта
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String ingredients = ingredientsEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (title.isEmpty() || category.isEmpty() || description.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = getBitmapFromImageView(recipeImageView);
        if (bitmap == null) {
            Toast.makeText(this, "Выберите изображение!", Toast.LENGTH_SHORT).show();
            return;
        }

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setEnabled(false);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Сохранение...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            if (recipeId == -1) {
                dbHelper.addRecipe(title, description, ingredients, category, bitmap);
            } else {
                dbHelper.updateRecipe(recipeId, title, description, ingredients, category, bitmap);
            }

            runOnUiThread(() -> {
                progressDialog.dismiss();
                finish();
            });
        }).start();
    }
    private Bitmap getBitmapFromImageView(ImageView imageView) { //получение изображения
        if (imageView.getDrawable() instanceof BitmapDrawable) {
            return ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }
        return null;
    }
    private void loadRecipe() { //загрузка изображения
        Cursor cursor = dbHelper.getRecipeById(recipeId);
        if (cursor.moveToFirst()) {
            titleEditText.setText(cursor.getString(1));
            descriptionEditText.setText(cursor.getString(2));
            ingredientsEditText.setText(cursor.getString(3));
            //noinspection rawtypes,unchecked
            categorySpinner.setSelection(((ArrayAdapter) categorySpinner.getAdapter()).getPosition(cursor.getString(4)));

            Bitmap image = dbHelper.getRecipeImage(recipeId);
            if (image != null) {
                recipeImageView.setImageBitmap(image);
            }
        }
        cursor.close();
    }
}