package com.fsmsh.checkpad.util;

import com.fsmsh.checkpad.model.Tarefa;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sort {
    public static final int ORDEM_CRESCENTE = 1;
    public static final int ORDEM_DECRESCENTE = -1;

    public static List<Tarefa> sortByPriority(List<Tarefa> tarefas, int ordem) {
        tarefas.sort(Comparator.comparing(Tarefa::getPrioridade));

        if (ordem == ORDEM_CRESCENTE) Collections.reverse(tarefas);

        return tarefas;
    }
}
