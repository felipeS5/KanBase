package com.fsmsh.checkpad.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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


    // Notificações
    public static int getRandomID() {
        int randomID = new Random().nextInt();

        JSONArray array = new JSONArray();
        try {
            if (!getNotificacoes().equals("")) array = new JSONArray(getNotificacoes());

            JSONObject obj;

            for (int i = 0; i < array.length(); ++i) {
                obj = array.getJSONObject(i);
                int notID = obj.getInt("notificationID");

                if (notID == randomID) return 0;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        return randomID;
    }

    public static void addNotificacao(String tarefaID, int notificationID) {
        JSONArray array = new JSONArray();
        String arrayStr;
        try {
            if (!getNotificacoes().equals("")) array = new JSONArray(getNotificacoes());

            JSONObject obj = new JSONObject();

            obj.put("tarefaID", tarefaID);
            obj.put("notificationID", notificationID);

            array.put(obj);


            arrayStr = array.toString();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SharedPreferences preferences = context.getSharedPreferences("notifications.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putString("notifications", arrayStr);

        prefEditor.apply();
    }

    public static String getNotificacoes() {
        SharedPreferences preferences = context.getSharedPreferences("notifications.pref", MODE_PRIVATE);
        return preferences.getString("notifications", "");
    }

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
