package com.example.recipeapp.api;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
public class AuthInterceptor implements Interceptor {
    private String supabaseKey;
    public AuthInterceptor(String supabaseKey) {
        this.supabaseKey = supabaseKey;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .header("apikey", supabaseKey)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        return chain.proceed(request);
    }
}
