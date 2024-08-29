package com.fsmsh.checkpad.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.EditActivity;
import com.fsmsh.checkpad.model.Tarefa;

import java.util.Random;

public class NotificationHelper {

    Context context;
    public static final String VENCENDO_CHANNEL_ID = "expiring_today_channel";
    public static final String VENCENDO_CHANNEL_NAME = "Expirando hoje";
    public static final String VENCENDO_CHANNEL_DESCRIPTION = "Tarefas que estão expirando no dia atual";

    public NotificationHelper(Context context) {
        this.context = context;
    }


    public void configurarChannel() {
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel vencendoNotificationChannel;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            vencendoNotificationChannel = new NotificationChannel(VENCENDO_CHANNEL_ID, VENCENDO_CHANNEL_NAME, importance);
            vencendoNotificationChannel.setDescription(VENCENDO_CHANNEL_DESCRIPTION);
            mNotificationManager.createNotificationChannel(vencendoNotificationChannel);
        }
    }

    public void enviarNotificacao(Tarefa tarefa) {
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.header_icon)
                .setContentTitle(tarefa.getTarefaNome())
                .setContentText(context.getString(R.string.a_tarefa_ira_vencer_em_pouco_tempo, tarefa.getTarefaNome()))
                .setChannelId(VENCENDO_CHANNEL_ID)
                .setAutoCancel(true); // clear notification when clicked

        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("isNovo", false);
        intent.putExtra("id", tarefa.getId());
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setContentIntent(pi);

        // Evita ids duplicados
        int randomID = MyPreferences.getRandomID();
        while (randomID == 0) {
            randomID = MyPreferences.getRandomID();
        }

        mNotificationManager.notify(randomID, mBuilder.build());

        MyPreferences.addNotificacao(tarefa.getId(), randomID);
    }


}