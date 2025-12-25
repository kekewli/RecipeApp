package com.example.recipeapp.model;
import com.google.gson.annotations.SerializedName;
public class UserRecipeStorage {
    @SerializedName("id")
    public int id;
    @SerializedName("user_id")
    public int user_id;
    @SerializedName("recipe_id")
    public int recipe_id;
}