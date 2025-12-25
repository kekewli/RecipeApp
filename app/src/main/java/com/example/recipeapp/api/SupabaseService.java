package com.example.recipeapp.api;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.Rating;
import com.example.recipeapp.model.UserRequest;
import com.example.recipeapp.model.LoginResponse;
import java.util.List;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface SupabaseService {
    //АВТОРИЗАЦИЯ
    @POST("/rest/v1/rpc/register_user")
    Call<Void> registerUser(@Body Map<String, Object> params);
    @POST("/rest/v1/rpc/login_user")
    Call<List<LoginResponse>> loginUser(@Body Map<String, Object> params);
    @POST("/rest/v1/rpc/create_password_reset_token")
    Call<Map<String, Object>> createPasswordResetToken(@Body Map<String, Object> params);
    @POST("/rest/v1/rpc/reset_password")
    Call<Map<String, Object>> resetPassword(@Body Map<String, Object> params);
    //РЕЦЕПТЫ
    @GET("/rest/v1/recipes")
    Call<List<Recipe>> getAllRecipes();
    @POST("/rest/v1/rpc/get_recipe_details")
    Call<List<Recipe>> getRecipeDetailsRPC(@Body Map<String, Object> params);
    @POST("/rest/v1/rpc/add_recipe")
    Call<Map<String, Object>> addRecipe(@Body Map<String, Object> params);
    @PATCH("/rest/v1/recipes?id=eq.{id}")
    Call<Map<String, Object>> updateRecipe(@Path("id") int id, @Body Map<String, Object> params);
    @DELETE("/rest/v1/recipes?id=eq.{id}")
    Call<Void> deleteRecipe(@Path("id") int id);
    //РЕЙТИНГИ
    @POST("/rest/v1/rpc/rate_recipe")
    Call<Map<String, Object>> rateRecipe(@Body Map<String, Object> params);
    @GET("/rest/v1/recipe_ratings?user_id=eq.{userId}&recipe_id=eq.{recipeId}")
    Call<List<Rating>> getUserRating(@Path("userId") int userId, @Path("recipeId") int recipeId);
    //ИЗБРАННОЕ
    @POST("/rest/v1/rpc/add_recipe_to_user_storage")
    Call<Boolean> addRecipeToFavorites(@Body Map<String, Object> params);
    @POST("/rest/v1/rpc/get_user_recipes")
    Call<List<Recipe>> getUserRecipes(@Body Map<String, Object> params);
    @POST("/rest/v1/rpc/delete_recipe_from_user_storage")
    Call<Void> deleteRecipeFromFavorites(@Body Map<String, Object> params);
    //ПОЛЬЗОВАТЕЛЬСКИЕ ЗАПРОСЫ
    @POST("/rest/v1/rpc/create_user_request")
    Call<Void> createUserRequest(@Body Map<String, Object> params);
    @POST("/rest/v1/user_requests")
    Call<Map<String, Object>> addUserRequest(@Body Map<String, Object> params);
    @GET("/rest/v1/user_requests")
    Call<List<UserRequest>> getUserRequests();
    @PATCH("/rest/v1/user_requests?id=eq.{id}")
    Call<Map<String, Object>> approveUserRequest(@Path("id") int id, @Body Map<String, Object> params);
    @DELETE("/rest/v1/user_requests?id=eq.{id}")
    Call<Void> rejectUserRequest(@Path("id") int id);
    //ХРАНИЛИЩЕ ФАЙЛОВ
    @POST("/storage/v1/object/{bucket}/{path}")
    Call<Void> uploadFile(@Path("bucket") String bucket, @Path("path") String path, @Body RequestBody file);
}