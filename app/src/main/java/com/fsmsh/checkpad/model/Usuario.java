package com.fsmsh.checkpad.model;

import com.fsmsh.checkpad.util.Database;

import java.util.List;

public class Usuario {

    List<Tarefa> tarefas = Database.getTarefas(Database.PROGRESS_TODOS);
    String nome = "Felipe S";
    Integer idade = 23;
    String firestoreDocId = "dcjkhf737hd";

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    public String getFirestoreDocId() {
        return firestoreDocId;
    }

    public void setFirestoreDocId(String firestoreDocId) {
        this.firestoreDocId = firestoreDocId;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
