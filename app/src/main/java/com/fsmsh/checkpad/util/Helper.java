package com.fsmsh.checkpad.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.fsmsh.checkpad.activities.settings.SettingsActivity;

import java.util.Locale;

public class Helper {


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
