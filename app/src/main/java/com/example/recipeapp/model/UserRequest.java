package com.example.recipeapp.model;
import com.google.gson.annotations.SerializedName;
public class UserRequest {
    @SerializedName("id")
    public int id;
    @SerializedName("user_id")
    public int user_id;
    @SerializedName("recipe_name")
    public String recipe_name;
    @SerializedName("description")
    public String description;
    @SerializedName("ingredients")
    public String ingredients;
    @SerializedName("category_id")
    public int category_id;
    @SerializedName("image_url")
    public String image_url;
    @SerializedName("status")
    public String status;
}