package com.example.recipeapp.model;
import com.google.gson.annotations.SerializedName;
public class Recipe {
    @SerializedName("recipe_id")
    private int id;
    @SerializedName("recipe_name")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("ingredients")
    private String ingredients;
    @SerializedName("category_name")
    private String category;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("coalesce")
    private double averageRating;
    public Recipe() {}
    public Recipe(int id, String title, String description, String ingredients,
                  String category, String imageUrl, double averageRating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.category = category;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
    }
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getIngredients() {
        return ingredients;
    }
    public String getCategory() {
        return category;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public double getAverageRating() {
        return averageRating;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}