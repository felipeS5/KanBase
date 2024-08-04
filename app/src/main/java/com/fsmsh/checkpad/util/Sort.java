package com.fsmsh.checkpad.util;

import com.fsmsh.checkpad.model.Tarefa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sort {
    public static final int ORDEM_CRESCENTE = 1;
    public static final int ORDEM_DECRESCENTE = -1;

    public static List<Tarefa> sortByPriority(List<Tarefa> tarefas, int ordem) {
        tarefas.sort(Comparator.comparing(Tarefa::getPrioridade));

        if (ordem == ORDEM_DECRESCENTE) Collections.reverse(tarefas);

        return tarefas;
    }

    public static List<Tarefa> sortByCreation(List<Tarefa> tarefas, int ordem) {

        if (ordem == ORDEM_DECRESCENTE) Collections.reverse(tarefas);

        return tarefas;
    }

    public static List<Tarefa> filtrar(List<Tarefa> tarefas, boolean[] estadosAtivos, List<String> tagsAtivas, boolean aceitarSemTags) {
        List<Tarefa> tarefasFiltradas = new ArrayList<>();

        for (Tarefa tarefa : tarefas) {
            boolean isProgressOK = false;
            boolean isTagOK = false;

            // estado notStarted
            if ( (tarefa.getProgresso() == Database.PROGRESS_NAO_INICIADO) && estadosAtivos[Database.PROGRESS_NAO_INICIADO]) {
                isProgressOK = true;
            }
            // estado started
            if ( (tarefa.getProgresso() == Database.PROGRESS_INICIADO) && estadosAtivos[Database.PROGRESS_INICIADO]) {
                isProgressOK = true;
            }
            // estado finished
            if ( (tarefa.getProgresso() == Database.PROGRESS_COMPLETO) && estadosAtivos[Database.PROGRESS_COMPLETO]) {
                isProgressOK = true;
            }

            // Checagem de tags
            if (isProgressOK) {
                String[] tagsArr = tarefa.getCategoria().split("‖");
                List<String> tagsDaTarefa = new ArrayList<>();
                for (String s : tagsArr) if (!s.equals("")) tagsDaTarefa.add(s);

                if ((tagsDaTarefa.size() == 0) && aceitarSemTags) isTagOK = true;

                for (String tagTarefa : tagsDaTarefa) {
                    for (String tagAtiva : tagsAtivas) {
                        if (tagTarefa.equals(tagAtiva)) isTagOK = true;
                    }
                }

            }

            // Checagem para ver se passou pelo filtro
            if (isProgressOK && isTagOK) tarefasFiltradas.add(tarefa);

        }

        return tarefasFiltradas;

    }
}
