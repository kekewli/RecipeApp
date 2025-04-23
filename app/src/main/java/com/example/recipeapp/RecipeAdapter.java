package com.example.recipeapp;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Locale;
public class RecipeAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> recipes;
    private final ArrayList<Integer> recipeIds;
    private String searchQuery = "";

    public RecipeAdapter(Context context, ArrayList<String> recipes, ArrayList<Integer> recipeIds) {
        this.context = context;
        this.recipes = recipes;
        this.recipeIds = recipeIds;
    }

    public void updateSearchQuery(String query) {
        this.searchQuery = query.toLowerCase(Locale.ROOT);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recipeIds.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        String recipeTitle = recipes.get(position);

        if (!searchQuery.isEmpty()) {
            SpannableString spannable = new SpannableString(recipeTitle);
            int index = recipeTitle.toLowerCase(Locale.ROOT).indexOf(searchQuery);

            // Определяем цвет в зависимости от темы
            int highlightColor;
            if (isDarkTheme(context)) {
                highlightColor = Color.parseColor("#BB86FC"); // Для темной темы
            } else {
                highlightColor = Color.parseColor("#BB86FC"); // Для светлой темы
            }

            while (index >= 0) {
                spannable.setSpan(new BackgroundColorSpan(highlightColor), index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = recipeTitle.toLowerCase(Locale.ROOT).indexOf(searchQuery, index + searchQuery.length());
            }

            textView.setText(spannable);
        } else {
            textView.setText(recipeTitle);
        }
        return convertView;
    }

    private boolean isDarkTheme(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }
}