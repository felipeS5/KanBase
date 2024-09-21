package com.fsmsh.checkpad.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.fsmsh.checkpad.activities.settings.SettingsActivity;
import com.fsmsh.checkpad.model.Tarefa;

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
