package com.example.recipeapp.api;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class SupabaseClient {
    private static final String SUPABASE_URL = "https://mrwucqzxbhmxsyfgqgwn.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1yd3VjcXp4YmhteHN5ZmdxZ3duIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk3MzA3MjgsImV4cCI6MjA2NTMwNjcyOH0.JR9e-Q3GH7QjI3DSOTAyR-Y9QeWJ5Dj71wWBvBoVWKw";
    private static Retrofit retrofit;
    private static SupabaseService instance;
    public static SupabaseService getInstance() {
        if (instance == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(SUPABASE_KEY))
                    .addInterceptor(loggingInterceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(SupabaseService.class);
        }
        return instance;
    }
}