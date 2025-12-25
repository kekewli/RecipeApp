package com.example.recipeapp.data;
import android.app.Application;
import com.example.recipeapp.api.SupabaseClient;
public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SupabaseClient.getInstance();
    }
}