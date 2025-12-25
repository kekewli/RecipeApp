package com.example.recipeapp.data;
import android.util.Log;
import com.example.recipeapp.model.UserRecipeRequest;
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
public class UserRecipeRequestRepository {
    private static final String TAG = "UserRecipeRequestRepository";
    private static final String TABLE_REQUESTS = "user_recipe_requests";
    private final OkHttpClient client;
    private final Gson gson;
    public UserRecipeRequestRepository() {
        this.client = new OkHttpClient.Builder().build();
        this.gson = new Gson();
    }
    private HttpUrl.Builder baseUrlBuilder() {
        HttpUrl url = HttpUrl.parse(Constants.SUPABASE_URL + "/rest/v1/" + TABLE_REQUESTS);
        if (url == null) {
            throw new IllegalStateException("Invalid SUPABASE_URL");
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
    public void createRequest(UserRecipeRequest request) throws IOException {
        HttpUrl url = baseUrlBuilder().build();

        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request httpRequest = baseRequestBuilder(url)
                .post(body)
                .addHeader("Prefer", "return=representation")
                .build();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "createRequest error: " + response.code());
                throw new IOException("createRequest failed: " + response.code());
            }
        }
    }
    public List<UserRecipeRequest> getUserRequests(int userId) throws IOException {
        HttpUrl url = baseUrlBuilder()
                .addQueryParameter("user_id", "eq." + userId)
                .build();
        Request request = baseRequestBuilder(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "getUserRequests error: " + response.code());
                throw new IOException("getUserRequests failed: " + response.code());
            }
            String body = response.body() != null ? response.body().string() : "[]";
            UserRecipeRequest[] requests = gson.fromJson(body, UserRecipeRequest[].class);
            List<UserRecipeRequest> result = new ArrayList<>();
            if (requests != null) {
                for (UserRecipeRequest req : requests) {
                    if (req != null) {
                        result.add(req);
                    }
                }
            }
            return result;
        }
    }
    public UserRecipeRequest getRequestById(int id) throws IOException {
        HttpUrl url = baseUrlBuilder()
                .addQueryParameter("id", "eq." + id)
                .build();
        Request request = baseRequestBuilder(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("getRequestById failed: " + response.code());
            }
            String body = response.body() != null ? response.body().string() : "[]";
            UserRecipeRequest[] requests = gson.fromJson(body, UserRecipeRequest[].class);
            if (requests != null && requests.length > 0) {
                return requests[0];
            }
            return null;
        }
    }
}

