package com.fsmsh.checkpad.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Tarefa;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String tarefaID = intent.getStringExtra("tarefaID");
        String notifyType = intent.getStringExtra("notifyType");

        new Database(context); // Inicializei só pra passar o context
        Tarefa tarefa = Database.getTarefa(tarefaID);

        if (tarefa != null) {
            String mensagem = "";
            if (notifyType.equals("start") && tarefa.getProgresso()==0) {
                if (tarefa.getNotifyBefore()<60) {
                    mensagem = context.getString(R.string.a_tarefa_x_deve_ser_iniciada_em_x_minutos, tarefa.getTarefaNome(), tarefa.getNotifyBefore());
                } else {
                    mensagem = context.getString(R.string.a_tarefa_x_deve_ser_iniciada_em_1_hora, tarefa.getTarefaNome());
                }
            } else if (notifyType.equals("limit") && tarefa.getProgresso()<2) {
                if (tarefa.getNotifyBefore()<60) {
                    mensagem = context.getString(R.string.a_tarefa_x_ira_expirar_em_x_minutos, tarefa.getTarefaNome(), tarefa.getNotifyBefore());
                } else {
                    mensagem = context.getString(R.string.a_tarefa_x_ira_expirar_em_1_hora, tarefa.getTarefaNome());
                }
            }

            if (!mensagem.equals("")) {
                NotificationHelper notificationHelper = new NotificationHelper(context);
                notificationHelper.enviarNotificacao(tarefa.getTarefaNome(), mensagem, tarefaID);
            }
        }

    }
}
