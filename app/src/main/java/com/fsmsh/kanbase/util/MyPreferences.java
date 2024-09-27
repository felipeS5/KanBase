package com.fsmsh.kanbase.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import java.time.LocalTime;

public class MyPreferences {

    private static Context context;

    public MyPreferences(Context context) {
        this.context = context;
    }


    // Permissões
    public static void setPermissionFirstDenied(boolean estado) {
        SharedPreferences preferences = context.getSharedPreferences("permission.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putBoolean("is_permission_denied_first_time", estado);
        prefEditor.apply();
    }

    public static boolean isPermissionFirstDenied() {
        SharedPreferences preferences = context.getSharedPreferences("permission.pref", MODE_PRIVATE);
        return preferences.getBoolean("is_permission_denied_first_time", false);
    }

    // Resumo diário
        // Sumary No Tasks
    public static void setSumaryNoTasksActive(boolean estado) {
        SharedPreferences preferences = context.getSharedPreferences("daily_sumary.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putBoolean("is_sumary_no_tasks_active", estado);
        prefEditor.apply();
    }

    public static boolean isSumaryNoTasksActive() {
        SharedPreferences preferences = context.getSharedPreferences("daily_sumary.pref", MODE_PRIVATE);
        return preferences.getBoolean("is_sumary_no_tasks_active", true);
    }

        // isActive
    public static void setDailySumaryActive(boolean estado) {
        SharedPreferences preferences = context.getSharedPreferences("daily_sumary.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putBoolean("is_active", estado);
        prefEditor.apply();
    }

    public static boolean isDailySumaryActive() {
        SharedPreferences preferences = context.getSharedPreferences("daily_sumary.pref", MODE_PRIVATE);
        return preferences.getBoolean("is_active", true);
    }

        // localTime
    public static void setDailySumaryLocalTime(LocalTime localTime) {
        SharedPreferences preferences = context.getSharedPreferences("daily_sumary.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putInt("hour", localTime.getHour());
        prefEditor.putInt("min", localTime.getMinute());
        prefEditor.apply();
    }

    public static LocalTime getDailySumaryLocalTime() {
        SharedPreferences preferences = context.getSharedPreferences("daily_sumary.pref", MODE_PRIVATE);

        int hour = preferences.getInt("hour", 8);
        int min = preferences.getInt("min", 00);

        return LocalTime.of(hour, min);
    }

    // Notificação prévia padrão
    public static void setDefaultNotify(int notifyBefore) {
        SharedPreferences preferences = context.getSharedPreferences("notify.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putInt("default", notifyBefore);
        prefEditor.apply();
    }

    public static int getDefaultNotify() {
        SharedPreferences preferences = context.getSharedPreferences("notify.pref", MODE_PRIVATE);
        return preferences.getInt("default", 15);
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
