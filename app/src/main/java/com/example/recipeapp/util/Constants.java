package com.example.recipeapp.util;
public class Constants {
    // Supabase
    public static final String SUPABASE_URL = "https://mrwucqzxbhmxsyfgqgwn.supabase.co";
    public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1yd3VjcXp4YmhteHN5ZmdxZ3duIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk3MzA3MjgsImV4cCI6MjA2NTMwNjcyOH0.JR9e-Q3GH7QjI3DSOTAyR-Y9QeWJ5Dj71wWBvBoVWKw";
    // Таблицы
    public static final String TABLE_USERS = "users";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RATINGS = "recipe_ratings";
    public static final String TABLE_FAVORITES = "user_recipe_storage";
    public static final String TABLE_REQUESTS = "recipe_requests";
    public static final String STORAGE_BUCKET_RECIPES = "recipe_images";
    public static final String RPC_LOGIN = "login_user";
    public static final String RPC_REGISTER = "register_user";
    public static final String RPC_ADD_RECIPE = "add_recipe";
    public static final String RPC_GET_AVERAGE_RATING = "get_avg_rating";
    // Валидация
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 300;
    public static final int MAX_INGREDIENTS_LENGTH = 300;
    // Изображение
    public static final int MAX_IMAGE_WIDTH = 850;
    public static final int MAX_IMAGE_HEIGHT = 850;
    public static final int IMAGE_QUALITY = 90;
    // UI Константы
    public static final int SEARCH_DEBOUNCE_MS = 500;
    public static final int TOAST_DURATION = 2000;
}
