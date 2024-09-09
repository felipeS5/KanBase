package com.fsmsh.checkpad.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.fsmsh.checkpad.activities.main.MainActivity;
import com.fsmsh.checkpad.model.Tarefa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MyPreferences {

    private static Context context;

    public MyPreferences(Context context) {
        this.context = context;
    }


    // Tema
    public static void setTema(int tema) {
        SharedPreferences preferences = context.getSharedPreferences("tema.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putInt("tema", tema);

        Log.d("TAG", "setTema: "+tema);

        prefEditor.apply();
    }

    public static int getTema() {
        int tema = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

        SharedPreferences preferences = context.getSharedPreferences("tema.pref", MODE_PRIVATE);

        if (preferences.contains("tema")) {
            tema = preferences.getInt("tema", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        Log.d("TAG", "getTema: "+tema);

        return tema;
    }


    // Classificação de itens
    public static void salvarPreferenciasClassify(String classifyType, int ordem) {
        SharedPreferences preferences = context.getSharedPreferences("classify.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putString("classifyType", classifyType);
        prefEditor.putInt("ordem", ordem);

        prefEditor.apply();
    }

    // Sincronização com o banco de dados
    public static void setSincronizado(boolean sincronizado) {
        SharedPreferences preferences = context.getSharedPreferences("sync.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putBoolean("isSincronizado", sincronizado);

        prefEditor.apply();
    }

    public static boolean isSincronizado() {
        SharedPreferences preferences = context.getSharedPreferences("sync.pref", MODE_PRIVATE);
        return preferences.getBoolean("isSincronizado", true);
    }
}
