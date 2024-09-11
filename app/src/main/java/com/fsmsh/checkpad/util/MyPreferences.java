package com.fsmsh.checkpad.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class MyPreferences {

    private static Context context;

    public MyPreferences(Context context) {
        this.context = context;
    }


    // Pending Restart
    public static void setPendingRestart(boolean pendingRestart) {
        SharedPreferences preferences = context.getSharedPreferences("sys.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putBoolean("restart", pendingRestart);
        prefEditor.apply();
    }

    public static boolean isPendingRestart() {
        SharedPreferences preferences = context.getSharedPreferences("sys.pref", MODE_PRIVATE);
        return preferences.getBoolean("restart", false);
    }

    // Idioma
    public static void setIdioma(String lang) {
        SharedPreferences preferences = context.getSharedPreferences("lang.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putString("lang", lang);
        prefEditor.apply();
    }

    public static String getIdioma() {
        SharedPreferences preferences = context.getSharedPreferences("lang.pref", MODE_PRIVATE);
        return preferences.getString("lang", "pt_br");
    }

    // Tema
    public static void setTema(int tema) {
        SharedPreferences preferences = context.getSharedPreferences("tema.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putInt("tema", tema);
        prefEditor.apply();
    }

    public static int getTema() {
        int tema = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

        SharedPreferences preferences = context.getSharedPreferences("tema.pref", MODE_PRIVATE);

        if (preferences.contains("tema")) {
            tema = preferences.getInt("tema", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

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
