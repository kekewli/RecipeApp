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

        TextView appTitleTextView = findViewById(R.id.appTitleTextView); //код гойды Z
        TextView easterEggZ = findViewById(R.id.easterEggZ);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.goida);
        final int[] tapCount = {0};
        final long[] lastTapTime = {0};
        appTitleTextView.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime[0] > 2000) {
                tapCount[0] = 0;
                easterEggZ.setAlpha(0f);
                easterEggZ.setVisibility(View.INVISIBLE);
                easterEggZ.setTextColor(Color.BLACK);
                easterEggZ.getPaint().setShader(null);
            }
            tapCount[0]++;
            lastTapTime[0] = currentTime;
            if (tapCount[0] == 10) {
                easterEggZ.setVisibility(View.VISIBLE);
                easterEggZ.setText("Z");
                easterEggZ.setAlpha(1f);
                Shader textShader = new LinearGradient(
                        0, 0, 0, easterEggZ.getTextSize(),
                        new int[]{
                                Color.parseColor("#FFFFFF"),
                                Color.parseColor("#FFFFFF"),
                                Color.parseColor("#0000FF"),
                                Color.parseColor("#0000FF"),
                                Color.parseColor("#FF0000"),
                                Color.parseColor("#FF0000")
                        },
                        new float[]{
                                0f, 0.55f, 0.56f, 0.84f, 0.86f, 1f
                        },
                        Shader.TileMode.CLAMP
                );
                easterEggZ.getPaint().setShader(textShader);
                easterEggZ.invalidate();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    easterEggZ.setVisibility(View.INVISIBLE);
                    easterEggZ.setAlpha(0f);
                    easterEggZ.getPaint().setShader(null);
                    easterEggZ.invalidate();
                });
            }
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