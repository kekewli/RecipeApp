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
    //Конструктор адаптера
    public RecipeAdapter(Context context, ArrayList<String> recipes, ArrayList<Integer> recipeIds) {
        this.context = context;
        this.recipes = recipes;
        this.recipeIds = recipeIds;
    }
    //Обновление поисковой строки и списка под тему устройства
    public void updateSearchQuery(String query) {
        this.searchQuery = query.toLowerCase(Locale.ROOT);
        notifyDataSetChanged();
    }
    //Возвращает кол-во элементов в списке
    @Override
    public int getCount() {
        return recipes.size();
    }
    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }
    //Возвращает рецепт в указанную позицию
    @Override
    public long getItemId(int position) {
        return recipeIds.get(position);
    }
    /*Создает или переиспользует View для элемента списка, вставляет в него название рецепта
     и, если есть поисковый запрос, подсвечивает совпадающие подстроки.
     */
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
            // Определяет цвет в зависимости от темы
            int highlightColor;// Для светлой темы
            highlightColor = Color.parseColor("#BB86FC"); // Для темной темы
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
    //Проверка, включена ли в системе тёмная тема.
    private boolean isDarkTheme(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }
}