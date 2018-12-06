package com.example.user.trendy.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FilterSharedPreference {


    public static final String Data = "hello";

    public static void saveInSp(String key,boolean value,Context context){
        SharedPreferences preferences = context.getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static void saveInSp_sort(String key,boolean value,Context context){
        SharedPreferences preferences = context.getSharedPreferences("sort_by", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static void saveInSp_price(String key,boolean value,Context context){
        SharedPreferences preferences = context.getSharedPreferences("price", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static void saveData(String key, String value , Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Data, Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getData(String key, Context context){
        SharedPreferences prefs = context.getSharedPreferences(Data, Activity.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void saveArrayList(ArrayList<String> list, String key,Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getArrayList(String key,Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
