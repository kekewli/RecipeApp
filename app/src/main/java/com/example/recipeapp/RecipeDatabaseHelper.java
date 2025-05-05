package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_RECIPES = "recipes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_IMAGE = "imagePath";

    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "ingredients TEXT, " +
                "category TEXT, " +
                "image BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipes");
        onCreate(db);
    }

    public void addRecipe(String title, String description, String ingredients, String category, Bitmap image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("ingredients", ingredients);
        values.put("category", category);
        values.put("image", getBytesFromBitmap(image));

        long result = db.insert("recipes", null, values);
        db.close();

        if (result == -1) {
            Log.e("DB_ERROR", "Ошибка при добавлении рецепта!");
        } else {
            Log.d("DB_DEBUG", "Рецепт успешно добавлен с ID: " + result);
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void updateRecipe(int id, String title, String description, String ingredients, String category, Bitmap image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("ingredients", ingredients);
        values.put("category", category);
        values.put("image", getBytesFromBitmap(image));

        int rowsAffected = db.update("recipes", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();

        if (rowsAffected == 0) {
            Log.e("DB_ERROR", "Ошибка при обновлении рецепта!");
        } else {
            Log.d("DB_DEBUG", "Рецепт обновлён с ID: " + id);
        }
    }

    public Cursor getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM recipes WHERE id = ?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECIPES, null, null, null, null, null, null);
    }
    public Cursor searchRecipes(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECIPES, null, COLUMN_TITLE + " LIKE ?",
                new String[]{"%" + query + "%"}, null, null, null);
    }
    public boolean deleteRecipe(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RECIPES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }
    public Bitmap getRecipeImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT image FROM recipes WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(0);
            cursor.close();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        cursor.close();
        return null;
    }
}