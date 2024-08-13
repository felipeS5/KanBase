package com.fsmsh.checkpad.model;

import com.fsmsh.checkpad.util.Database;

import java.util.List;

public class Usuario {

    List<Tarefa> tarefas;
    List<String> tags;
    String nome;
    String email;
    String firestoreDocId;

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getFirestoreDocId() {
        return firestoreDocId;
    }

    public void setFirestoreDocId(String firestoreDocId) {
        this.firestoreDocId = firestoreDocId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
