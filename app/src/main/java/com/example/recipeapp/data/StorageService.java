package com.example.recipeapp.data;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.example.recipeapp.api.SupabaseClient;
import com.example.recipeapp.api.SupabaseService;
import com.example.recipeapp.util.Constants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
public class StorageService {
    private static final String TAG = "StorageService";
    private final SupabaseService supabaseService;
    public StorageService(Context context) {
        this.supabaseService = SupabaseClient.getInstance();
    }
    public String uploadRecipeImage(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] data = stream.toByteArray();
        String filePath = "recipes/" + System.currentTimeMillis() + ".jpg";
        String imageUrl = Constants.SUPABASE_URL + "/storage/v1/object/public/"
                + Constants.STORAGE_BUCKET_RECIPES + "/" + filePath;
        Log.d(TAG, "Image URL: " + imageUrl);
        return imageUrl;
    }
}