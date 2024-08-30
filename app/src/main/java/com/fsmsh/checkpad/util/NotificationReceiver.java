package com.fsmsh.checkpad.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.EditActivity;
import com.fsmsh.checkpad.model.Tarefa;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String tarefaID = intent.getStringExtra("tarefaID");
        //new MyPreferences(context); // Inicializei só pra passar o context

        new Database(context); // Inicializei só pra passar o context
        Tarefa tarefa = Database.getTarefa(tarefaID);


        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.enviarNotificacao(tarefa);

    }
}
