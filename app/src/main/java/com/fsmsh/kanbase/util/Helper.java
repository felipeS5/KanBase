package com.fsmsh.kanbase.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fsmsh.kanbase.model.Tarefa;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Helper {


    public static int[] createNoRepeatedCodes() {
        List<Tarefa> tarefas = Database.getTarefas(Database.PROGRESS_TODOS);

        int code0 = new Random().nextInt();
        int code1 = new Random().nextInt();

        for (int i = 0; i < tarefas.size(); i++) {
            int codeStart = tarefas.get(i).getBroadcastCodeStart();
            int codeLimit = tarefas.get(i).getBroadcastCodeLimit();

            if (code0 == code1) {
                code0 = new Random().nextInt();
                code1 = new Random().nextInt();
                i = 0;
            }

            if (code0==codeStart || code0==codeLimit) {
                code0 = new Random().nextInt();
                i = 0;
            }

            if (code1==codeStart || code1==codeLimit) {
                code1 = new Random().nextInt();
                i = 0;
            }

        }

        return new int[]{code0, code1};
    }

    // Permissões
    public static void checkPermission(Activity activity) {
        if (!hasPermission(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 555);
        } else {
            MyPreferences.setPermissionFirstDenied(false);
        }

    }

    public static boolean hasPermission(Context context) {
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;

        }
    }

    // System
    public static void preConfigs(Context context) {
        new MyPreferences(context);

        // Idioma
        String idiomaAtual = MyPreferences.getIdioma();
        setLocale(context, idiomaAtual);

        // Tema
        int tema = MyPreferences.getTema();
        AppCompatDelegate.setDefaultNightMode(tema);

        // Resumo diário
        if (MyPreferences.isDailySumaryActive()) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.configurarChannel();
            notificationHelper.agendarNotificacaoDiaria();
        }
    }

    public static void setLocale(Context context, String lang) {
        String contry = Locale.getDefault().getCountry();
        Locale locale = new Locale(lang, contry);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        context.createConfigurationContext(config);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
