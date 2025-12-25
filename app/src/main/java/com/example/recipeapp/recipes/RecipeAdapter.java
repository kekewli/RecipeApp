package com.example.recipeapp.recipes;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.model.Recipe;
import java.util.List;
public class RecipeAdapter  extends ArrayAdapter<Recipe> {
    private final Context context;
    private final List<Recipe> recipes;
    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, 0, recipes);
        this.context = context;
        this.recipes = recipes;
    }
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
            holder = new ViewHolder();
            holder.recipeTitle = convertView.findViewById(R.id.recipeTitle);
            holder.recipeCategory = convertView.findViewById(R.id.recipeCategory);
            holder.recipeImage = convertView.findViewById(R.id.recipeImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Recipe recipe = recipes.get(position);
        if (recipe != null) {
            holder.recipeTitle.setText(recipe.getTitle());
            holder.recipeCategory.setText(recipe.getCategory());
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.camera)
                        .into(holder.recipeImage);
            } else {
                holder.recipeImage.setImageResource(R.drawable.camera);
            }
        }
        return convertView;
    }
    static class ViewHolder {
        TextView recipeTitle;
        TextView recipeCategory;
        ImageView recipeImage;
    }
}