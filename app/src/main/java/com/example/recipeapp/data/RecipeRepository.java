package com.example.recipeapp.data;
import android.content.Context;
import android.util.Log;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.util.Constants;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class RecipeRepository {
    private static final String TAG = "RecipeRepository";
    private final OkHttpClient client;
    private final Gson gson;
    public RecipeRepository(Context context) {
        this.client = new OkHttpClient.Builder().build();
        this.gson = new Gson();
    }
    private HttpUrl.Builder baseUrlBuilder() {
        HttpUrl url = HttpUrl.parse(Constants.SUPABASE_URL + "/rest/v1/" + Constants.TABLE_RECIPES);
        if (url == null) {
            throw new IllegalStateException("Invalid SUPABASE_URL or TABLE_RECIPES");
        }
        return url.newBuilder();
    }
    private Request.Builder baseRequestBuilder(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .addHeader("apikey", Constants.SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + Constants.SUPABASE_ANON_KEY)
                .addHeader("Content-Type", "application/json");
    }
    public Recipe getRecipeById(int id) throws IOException {
        HttpUrl url = baseUrlBuilder()
                .addQueryParameter("id", "eq." + id)
                .addQueryParameter("select", "*")
                .build();
        Request request = baseRequestBuilder(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "getRecipeById error: " + response.code() + " " + response.message());
                throw new IOException("getRecipeById failed: " + response.code());
            }
            String body = response.body() != null ? response.body().string() : "[]";
            Recipe[] recipes = gson.fromJson(body, Recipe[].class);
            if (recipes != null && recipes.length > 0) {
                return recipes[0];
            }
            return null;
        }
    }
    public void createRecipe(Recipe recipe) throws IOException {
        HttpUrl url = baseUrlBuilder()
                .build();
        String json = gson.toJson(recipe);
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );
        Request request = baseRequestBuilder(url)
                .post(body)
                .addHeader("Prefer", "return=representation")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "createRecipe error: " + response.code() + " " + response.message());
                throw new IOException("createRecipe failed: " + response.code());
            }
        }
    }
    public void updateRecipe(Recipe recipe) throws IOException {
        if (recipe.getId() <= 0) {
            throw new IllegalArgumentException("Recipe id must be > 0 for update");
        }
        HttpUrl url = baseUrlBuilder()
                .addQueryParameter("id", "eq." + recipe.getId())
                .build();
        String json = gson.toJson(recipe);
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );
        Request request = baseRequestBuilder(url)
                .patch(body)
                .addHeader("Prefer", "return=representation")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "updateRecipe error: " + response.code() + " " + response.message());
                throw new IOException("updateRecipe failed: " + response.code());
            }
        }
    }
    public void deleteRecipe(int id) throws IOException {
        HttpUrl url = baseUrlBuilder()
                .addQueryParameter("id", "eq." + id)
                .build();
        Request request = baseRequestBuilder(url)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "deleteRecipe error: " + response.code() + " " + response.message());
                throw new IOException("deleteRecipe failed: " + response.code());
            }
        }
    }
    public List<Recipe> getAllRecipes() throws IOException {
        HttpUrl url = baseUrlBuilder()
                .addQueryParameter("select", "*")
                .build();
        Request request = baseRequestBuilder(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "getAllRecipes error: " + response.code() + " " + response.message());
                throw new IOException("getAllRecipes failed: " + response.code());
            }
            String body = response.body() != null ? response.body().string() : "[]";
            Recipe[] recipes = gson.fromJson(body, Recipe[].class);
            List<Recipe> result = new ArrayList<>();
            if (recipes != null) {
                for (Recipe recipe : recipes) {
                    if (recipe != null) {
                        result.add(recipe);
                    }
                }
            }
            return result;
        }
    }
}