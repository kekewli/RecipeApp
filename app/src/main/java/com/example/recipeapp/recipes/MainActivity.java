package com.example.recipeapp.recipes;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Color;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.api.Supabase;
import com.example.recipeapp.model.Recipe;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    private RecipeAdapter adapter;
    private ArrayList<Recipe> allRecipes = new ArrayList<>();
    private ArrayList<Recipe> filteredRecipes = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);
        EditText searchEditText = findViewById(R.id.searchEditText);
        Button addRecipeButton = findViewById(R.id.addRecipeButton);
        Button favoritesButton = findViewById(R.id.favoritesButton);
        TextView appTitleTextView = findViewById(R.id.appTitleTextView);
        TextView easterEggZ = findViewById(R.id.easterEggZ);
        adapter = new RecipeAdapter(this, filteredRecipes);
        listView.setAdapter(adapter);
        loadAllRecipes();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        //Кнопка "Создать"
        addRecipeButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditRecipeActivity.class);
            startActivity(intent);
        });
        //Кнопка "Избранно"
        favoritesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < filteredRecipes.size()) {
                Recipe selectedRecipe = filteredRecipes.get(position);
                Intent intent = new Intent(MainActivity.this, AddEditRecipeActivity.class);
                intent.putExtra("RECIPE_ID", selectedRecipe.getId());
                startActivity(intent);
            }
        });
        mediaPlayer = MediaPlayer.create(this, R.raw.goida);
        final int[] tapCount = {0};
        final long[] lastTapTime = {0};
        appTitleTextView.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime[0] > 2000) {
                tapCount[0] = 0;
                easterEggZ.setAlpha(0f);
                easterEggZ.setVisibility(View.INVISIBLE);
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
                        new float[]{0f, 0.55f, 0.56f, 0.84f, 0.86f, 1f},
                        Shader.TileMode.CLAMP
                );
                easterEggZ.getPaint().setShader(textShader);
                easterEggZ.invalidate();
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
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
    protected void onResume() {
        super.onResume();
        loadAllRecipes();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "Остановлено");
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
    private void loadAllRecipes() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка рецептов...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Supabase.getAllRecipes(this,
                recipes -> {
                    allRecipes.clear();
                    allRecipes.addAll(recipes);
                    progressDialog.dismiss();
                    filterRecipes("");
                },
                () -> {
                    progressDialog.dismiss();
                }
        );
    }
    private void filterRecipes(String query) {
        filteredRecipes.clear();
        for (Recipe recipe : allRecipes) {
            String title = recipe.getTitle();
            if (title != null && !title.isEmpty() && title.toLowerCase().contains(query.toLowerCase())) {
                filteredRecipes.add(recipe);
            }
        }
        adapter.notifyDataSetChanged();
    }
}