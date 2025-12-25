package com.example.recipeapp.recipes;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.api.Supabase;
import com.example.recipeapp.model.Recipe;
import java.util.ArrayList;
public class FavoritesActivity extends AppCompatActivity {
    private RecipeAdapter adapter;
    private ArrayList<Recipe> favoriteRecipes = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private int currentUserId;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ListView listView = findViewById(R.id.favoritesListView);
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);
        if (currentUserId == -1) {
            finish();
            return;
        }
        adapter = new RecipeAdapter(this, favoriteRecipes);
        listView.setAdapter(adapter);
        loadFavoriteRecipes();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < favoriteRecipes.size()) {
                Recipe selectedRecipe = favoriteRecipes.get(position);
                Intent intent = new Intent(FavoritesActivity.this, AddEditRecipeActivity.class);
                intent.putExtra("RECIPE_ID", selectedRecipe.getId());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteRecipes();
    }
    private void loadFavoriteRecipes() {
        if (currentUserId == -1) {
            finish();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка избранного...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Supabase.getUserRecipes(this, currentUserId,
                recipes -> {
                    progressDialog.dismiss();
                    favoriteRecipes.clear();
                    favoriteRecipes.addAll(recipes);
                    adapter.notifyDataSetChanged();
                },
                () -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Ошибка загрузки избранного", Toast.LENGTH_SHORT).show();
                }
        );
    }
}