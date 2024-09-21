package com.fsmsh.checkpad.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.main.home.FragmentsIniciais;
import com.fsmsh.checkpad.model.Tarefa;

import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new Database(context); // Inicializei só pra passar o context
        new MyPreferences(context);

        if (intent.hasExtra("tarefaID")) {
            notificacaoDeTarefa(context, intent);
        } else {
            notificacaoDiaria(context, intent);
        }

    }

    public void notificacaoDiaria(Context context, Intent intent) {
        List<Tarefa> tarefas = Database.getTarefas(Database.PROGRESS_TODOS);
        int tarefasAtrazadas = 0;
        int tarefasVencendo = 0;

        for (Tarefa t : tarefas) {
            // Atarefa poderá ser encaixada nos 2 casos
            if (DateUtilities.isAtrazada(t) && t.getProgresso() != FragmentsIniciais.FINALIZADAS)
                tarefasAtrazadas++;
            if (DateUtilities.isVencendoHoje(t) && t.getProgresso() != FragmentsIniciais.FINALIZADAS)
                tarefasVencendo++;
        }

        if (tarefasAtrazadas > 0 || tarefasVencendo > 0) {
            String titulo = context.getString(R.string.resumo_diario);
            String mensagem = context.getString(R.string.voce_tem_2p);

            if (tarefasAtrazadas > 0) {
                String texto = "";

                if (tarefasAtrazadas == 1)
                    texto += tarefasAtrazadas + context.getString(R.string.sp_tarefa_atrazada);
                else texto += tarefasAtrazadas + context.getString(R.string.sp_tarefas_atrazadas);

                mensagem += texto;
            }

            // Conecta gramaticamente as palavras
            if (tarefasAtrazadas>0 && tarefasVencendo>0) mensagem += context.getString(R.string.sp_e_sp);

            if (tarefasVencendo > 0) {
                String texto = "";

                if (tarefasVencendo == 1)
                    texto += tarefasVencendo + context.getString(R.string.sp_tarefa_vencendo);
                else texto += tarefasVencendo + context.getString(R.string.sp_tarefas_vencendo);

                mensagem += texto;
            }

            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.enviarNotificacaoDiaria(titulo, mensagem);
            
        } else if (MyPreferences.isSumaryNoTasksActive()) {
            String titulo = context.getString(R.string.resumo_diario);
            String mensagem = context.getString(R.string.hoje_voce_nao_tem_tarefas_vencendo_ou_atrazadas);
            
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.enviarNotificacaoDiaria(titulo, mensagem);
            
        }

    }

    public void notificacaoDeTarefa(Context context, Intent intent) {
        String tarefaID = intent.getStringExtra("tarefaID");
        String notifyType = intent.getStringExtra("notifyType");

        Tarefa tarefa = Database.getTarefa(tarefaID);

        if (tarefa != null) {
            String mensagem = "";
            if (notifyType.equals("start") && tarefa.getProgresso()==0) { //todo Possibilidade de mandar "A tarefa x já devia ter sido iniciada"
                if (tarefa.getNotifyBefore()<60) {
                    mensagem = context.getString(R.string.a_tarefa_x_deve_ser_iniciada_em_x_minutos, tarefa.getTarefaNome(), tarefa.getNotifyBefore());
                } else {
                    mensagem = context.getString(R.string.a_tarefa_x_deve_ser_iniciada_em_1_hora, tarefa.getTarefaNome());
                }
            } else if (notifyType.equals("limit") && tarefa.getProgresso()<2) { //todo Possibilidade de mandar "A tarefa x está atrazada"
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
