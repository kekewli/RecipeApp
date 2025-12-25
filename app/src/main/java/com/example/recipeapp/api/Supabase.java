package com.example.recipeapp.api;

import com.example.recipeapp.model.LoginResponse;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.UserRequest;
import com.example.recipeapp.util.UtilityService;
import org.apache.commons.io.IOUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
public class Supabase {
    private static final String TAG = "SupabaseHelper";
    private static final String STORAGE_URL = "https://mrwucqzxbhmxsyfgqgwn.supabase.co";
    // Загрузка файлов
    public static void uploadFile(Context context, Uri fileUri, String bucket, String fileName,
                                  String mimeType, Consumer<String> onSuccess, Runnable onFailure) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            byte[] fileData = IOUtils.toByteArray(inputStream);
            RequestBody requestFile = RequestBody.create(fileData, MediaType.parse(mimeType));
            SupabaseService service = SupabaseClient.getInstance();
            service.uploadFile(bucket, fileName, requestFile).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String publicUrl = STORAGE_URL + "/storage/v1/object/public/" + bucket + "/" + fileName;
                        Log.d(TAG, "Файл загружен: " + publicUrl);
                        Toast.makeText(context, "Файл успешно загружен!", Toast.LENGTH_SHORT).show();
                        onSuccess.accept(publicUrl);
                    } else {
                        String errorBody = "";
                        try {
                            errorBody = response.errorBody() != null ? response.errorBody().string() : "пусто";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Ошибка загрузки: " + response.code() + " | " + errorBody);
                        Toast.makeText(context, "Ошибка загрузки файла", Toast.LENGTH_SHORT).show();
                        onFailure.run();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Ошибка сети: " + t.getMessage());
                    Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    onFailure.run();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при чтении файла: " + e.getMessage());
            onFailure.run();
        }
    }
    // Регистрация и авторизация
    public static void registerUser(Context context, String username, String passwordHash, String email,
                                    Runnable onSuccess, Runnable onFailure) {
        Log.d(TAG, "Username: " + username);
        Log.d(TAG, "Email: " + email);
        Map<String, Object> params = new HashMap<>();
        params.put("p_name", username);
        params.put("p_pass", passwordHash);
        params.put("p_email", email);
        SupabaseService service = SupabaseClient.getInstance();
        service.registerUser(params).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Успешная регистрация");
                    onSuccess.run();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "Ошибка: " + response.code() + " | " + errorBody);
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                t.printStackTrace();
                onFailure.run();
            }
        });
    }
    public static void loginUser(Context context, String username, String password,
                                 Runnable onSuccess, Runnable onFailure) {
        Log.d(TAG, "Username: " + username);
        String passwordHash = UtilityService.hashPassword(password);
        Log.d(TAG, "Password hash: " + passwordHash.substring(0, 10) + "...");
        Map<String, Object> params = new HashMap<>();
        params.put("p_name", username);
        params.put("p_pass", passwordHash);
        SafeContext safeContext = new SafeContext(context);
        SupabaseService service = SupabaseClient.getInstance();
        service.loginUser(params).enqueue(new Callback<List<LoginResponse>>() {
            @Override
            public void onResponse(Call<List<LoginResponse>> call, Response<List<LoginResponse>> response) {
                Log.d(TAG, "Code: " + response.code());
                Log.d(TAG, "Success: " + response.isSuccessful());
                Log.d(TAG, "Body is null: " + (response.body() == null));

                if (response.body() != null) {
                    Log.d(TAG, "Response body size: " + response.body().size());
                }

                if (!safeContext.isAlive()) {
                    onFailure.run();
                    return;
                }
                Context ctx = safeContext.get();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    try {
                        LoginResponse user = response.body().get(0);
                        Log.d(TAG, "Авторизация успешна");
                        Log.d(TAG, "User ID: " + user.user_id);
                        Log.d(TAG, "Role ID: " + user.role_id);

                        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("user_id", user.user_id);
                        editor.putInt("role_id", user.role_id);
                        editor.putString("username", username);
                        editor.apply();

                        Log.d(TAG, "SharedPreferences сохранен");

                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка: " + e.getMessage());
                        e.printStackTrace();
                        if (onFailure != null) {
                            onFailure.run();
                        }
                    }
                } else {
                    Log.e(TAG, "Неудачная авторизации");
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error: " + errorBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Ошибка: " + e.getMessage());
                    }

                    if (onFailure != null) {
                        onFailure.run();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<LoginResponse>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                t.printStackTrace();

                if (onFailure != null) {
                    onFailure.run();
                }
            }
        });
    }
    private static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String hash = hexString.toString();
            Log.d(TAG, "Password hashed: " + hash.substring(0, 10) + "...");
            return hash;
        } catch (Exception e) {
            Log.e(TAG, "Error hashing password: " + e.getMessage());
            return password;
        }
    }
    // Рецепты
    public static void getAllRecipes(Context context, Consumer<List<Recipe>> onSuccess, Runnable onFailure) {
        SafeContext safeContext = new SafeContext(context);
        SupabaseService service = SupabaseClient.getInstance();
        service.getAllRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (!safeContext.isAlive()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Получено " + response.body().size() + " рецептов");
                    onSuccess.accept(response.body());
                } else {
                    Log.e(TAG, "Ошибка получения рецептов: " + response.code());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    public static void addRecipe(Context context, String name, String description, String ingredients,
                                 int categoryId, String imageUrl, Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_name", name);
        params.put("p_desc", description);
        params.put("p_ingr", ingredients);
        params.put("p_cat_id", categoryId);
        params.put("p_image_url", imageUrl);
        SupabaseService service = SupabaseClient.getInstance();
        service.addRecipe(params).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Рецепт добавлен");
                    Toast.makeText(context, "Рецепт добавлен!", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Ошибка добавления: " + response.message());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    // Рейтинг
    public static void rateRecipe(Context context, int userId, int recipeId, int rating,
                                  Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user", userId);
        params.put("p_recipe", recipeId);
        params.put("p_rating", rating);
        SupabaseService service = SupabaseClient.getInstance();
        service.rateRecipe(params).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Оценка выставлена");
                    Toast.makeText(context, "Спасибо за оценку!", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Ошибка оценки: " + response.message());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    // Избранное
    public static void addRecipeToFavorites(Context context, int userId, int recipeId,
                                            Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user", userId);
        params.put("p_recipe", recipeId);
        SupabaseService service = SupabaseClient.getInstance();
        service.addRecipeToFavorites(params).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean result = response.body();
                    if (result != null && result) {
                        Toast.makeText(context, "Добавлено в избранное!", Toast.LENGTH_SHORT).show();
                        onSuccess.run();
                    } else {
                        Log.d(TAG, "Рецепт уже в избранном");
                        Toast.makeText(context, "Рецепт уже в избранном", Toast.LENGTH_SHORT).show();
                        onFailure.run();
                    }
                } else {
                    Log.e(TAG, "Ошибка добавления: " + response.code());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    public static void getUserRecipes(Context context, int userId,
                                      Consumer<List<Recipe>> onSuccess, Runnable onFailure) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user", userId);
        SupabaseService service = SupabaseClient.getInstance();
        service.getUserRecipes(params).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Получено " + response.body().size() + " избранных рецептов");
                    onSuccess.accept(response.body());
                } else {
                    Log.e(TAG, "Ошибка получения избранного: " + response.code());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    public static void removeRecipeFromFavorites(Context context, int userId, int recipeId,
                                                 Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user", userId);
        params.put("p_recipe", recipeId);
        SupabaseService service = SupabaseClient.getInstance();
        service.deleteRecipeFromFavorites(params).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Ошибка удаления: " + response.code());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    // Запросы
    public static void getUserRequests(Consumer<List<UserRequest>> onSuccess, Runnable onFailure) {
        SupabaseService service = SupabaseClient.getInstance();
        service.getUserRequests().enqueue(new Callback<List<UserRequest>>() {
            @Override
            public void onResponse(Call<List<UserRequest>> call, Response<List<UserRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Получено " + response.body().size() + " запросов");
                    onSuccess.accept(response.body());
                } else {
                    Log.e(TAG, "Ошибка получения запросов: " + response.code());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<List<UserRequest>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    public static void createUserRequest(Context context, int userId, String recipeName,
                                         String description, String ingredients, int categoryId,
                                         String imageUrl, Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user", userId);
        params.put("p_name", recipeName);
        params.put("p_desc", description);
        params.put("p_ingr", ingredients);
        params.put("p_cat_id", categoryId);
        params.put("p_image_url", imageUrl);
        SupabaseService service = SupabaseClient.getInstance();
        service.createUserRequest(params).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Запрос на рецепт создан");
                    Toast.makeText(context, "Запрос отправлен модератору!", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Ошибка создания запроса: " + response.code());
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show();
                onFailure.run();
            }
        });
    }
    // Восстановление
    public static void createPasswordResetToken(Context context, String email,
                                                Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_email", email);
        SupabaseService service = SupabaseClient.getInstance();
        service.createPasswordResetToken(params).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Письмо для восстановления отправлено");
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Ошибка отправки письма: " + response.code());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    // Удаление рецепта
    public static void deleteRecipe(Context context, int recipeId,
                                    Runnable onSuccess, Runnable onFailure) {
        SupabaseService service = SupabaseClient.getInstance();
        service.deleteRecipe(recipeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Рецепт удалён: " + recipeId);
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Ошибка удаления: " + response.code());
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при удалении: " + t.getMessage());
                onFailure.run();
            }
        });
    }
    // Получение детелай
    public static void getRecipeDetails(Context context, int recipeId,
                                        Consumer<Recipe> onSuccess, Runnable onFailure) {
        Log.d(TAG, "Recipe ID: " + recipeId);
        Map<String, Object> params = new HashMap<>();
        params.put("p_id", recipeId);
        SupabaseService service = SupabaseClient.getInstance();
        service.getRecipeDetailsRPC(params).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.d(TAG, "Код: " + response.code());
                Log.d(TAG, "Успех: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Recipe recipe = response.body().get(0);
                    Log.d(TAG, "Рецепт загружен: " + recipe.getTitle());
                    onSuccess.accept(recipe);
                } else {
                    Log.e(TAG, "Код: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Ошибка: " + errorBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Ошибка: " + e.getMessage());
                    }
                    onFailure.run();
                }
            }
            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Ошибка: " + t.getMessage());
                t.printStackTrace();
                onFailure.run();
            }
        });
    }
}