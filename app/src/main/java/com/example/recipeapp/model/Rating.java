package com.example.recipeapp.model;
import com.google.gson.annotations.SerializedName;
public class Rating{
    @SerializedName("id")
    public int id;
    @SerializedName("user_id")
    public int user_id;
    @SerializedName("recipe_id")
    public int recipe_id;
    @SerializedName("rating")
    public int rating;
}
