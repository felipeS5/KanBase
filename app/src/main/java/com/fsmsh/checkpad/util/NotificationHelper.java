package com.fsmsh.checkpad.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

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

    public void agendarNotificação(Tarefa tarefa) {
        long millisTime = System.currentTimeMillis() + 3000; // 3 segundos depois...
        long millisTime2 = System.currentTimeMillis() + 10000; // 10 segundos depois...

        // Intent para o BroadcastReceiver
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("tarefaID", tarefa.getId());

        if (!tarefa.getDateStart().equals("")) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tarefa.getBroadcastCodeStart(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Agendamento com AlarmManager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent);

                    Log.d("TAG", "agendarNotificação: Start"+tarefa.getBroadcastCodeStart());
                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){ // O app nem roda em KitKat!
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent);
                }
            }
        }

        if (!tarefa.getDateLimit().equals("")) {
            PendingIntent pendingIntentLimit = PendingIntent.getBroadcast(context, tarefa.getBroadcastCodeLimit(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Agendamento com AlarmManager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisTime2, pendingIntentLimit);

                    Log.d("TAG", "agendarNotificação: Limit"+tarefa.getBroadcastCodeLimit());

                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){ // O app nem roda em KitKat!
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, millisTime2, pendingIntentLimit);
                }
            }
        }



    }

    public void removerAgendamento(int[] broadcastCodes) {
        // Criando pendingIntent igual para remover
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, broadcastCodes[0], intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentLimit = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntentLimit = PendingIntent.getBroadcast(context, broadcastCodes[1], intentLimit, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.cancel(pendingIntentLimit);
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

        mNotificationManager.notify(new Random().nextInt(), mBuilder.build());

    }


}