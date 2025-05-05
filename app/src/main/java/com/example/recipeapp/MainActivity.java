package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import android.widget.ListView;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.media.MediaPlayer;
import android.widget.TextView;
import android.graphics.Color;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecipeDatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private ArrayList<String> recipeTitles;
    private ArrayList<Integer> recipeIds;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //открытие активити
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new RecipeDatabaseHelper(this);
        recipeTitles = new ArrayList<>();
        recipeIds = new ArrayList<>();

        ListView listView = findViewById(R.id.listView);
        EditText searchEditText = findViewById(R.id.searchEditText);
        FloatingActionButton addRecipeButton = findViewById(R.id.addRecipeButton);

        adapter = new RecipeAdapter(this, recipeTitles, recipeIds);
        listView.setAdapter(adapter);

        loadRecipes("");

        searchEditText.addTextChangedListener(new TextWatcher() { //поиск
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                loadRecipes(query);
                adapter.updateSearchQuery(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        searchEditText.setOnEditorActionListener((v, actionId, event) -> { //скрытие клавиатуры
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        addRecipeButton.setOnClickListener(view -> { //кнопка добавления рецепта
            Intent intent = new Intent(MainActivity.this, AddEditRecipeActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> { //открытие рецепти из списка
            Intent intent = new Intent(MainActivity.this, AddEditRecipeActivity.class);
            intent.putExtra("RECIPE_ID", recipeIds.get(position));
            startActivity(intent);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes("");
    }

    private void loadRecipes(String query) { //загрузка рецептов
        recipeTitles.clear();
        recipeIds.clear();

        Cursor cursor;
        if (query.isEmpty()) {
            cursor = dbHelper.getAllRecipes();
        } else {
            cursor = dbHelper.searchRecipes(query);
        }

        if (cursor.moveToFirst()) {
            do {
                recipeIds.add(cursor.getInt(0));
                recipeTitles.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
}