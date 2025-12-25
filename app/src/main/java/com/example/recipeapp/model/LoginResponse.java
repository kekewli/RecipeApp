package com.example.recipeapp.model;
import com.google.gson.annotations.SerializedName;
public class LoginResponse {
    @SerializedName("user_id")
    public int user_id;
    @SerializedName("role_id")
    public int role_id;
    public LoginResponse(int user_id, int role_id) {
        this.user_id = user_id;
        this.role_id = role_id;
    }
    public int getUserId() {
        return user_id;
    }
    public int getRoleId() {
        return role_id;
    }
}