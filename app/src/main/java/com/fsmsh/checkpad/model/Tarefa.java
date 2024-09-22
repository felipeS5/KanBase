package com.fsmsh.checkpad.model;

public class Tarefa {
    private String id;
    private String tarefaNome;
    private String descricao;
    private int progresso;
    private String dateStart;
    private String timeStart;
    private String dateLimit;
    private String timeLimit;
    private String categoria;
    private int prioridade;
    private int broadcastCodeStart; // Só existe pq preciso dele pra cancelar o broadcast
    private int broadcastCodeLimit; // Só existe pq preciso dele pra cancelar o broadcast
    private int notifyBefore; // Notificar X minutos antes
    private int notified; // Caso o usuário tenha sido notificado (0 ou 1)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTarefaNome() {
        return tarefaNome;
    }

    public void setTarefaNome(String tarefaNome) {
        this.tarefaNome = tarefaNome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getProgresso() {
        return progresso;
    }

    public void setProgresso(int progresso) {
        this.progresso = progresso;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(String dateLimit) {
        this.dateLimit = dateLimit;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public int getBroadcastCodeStart() {
        return broadcastCodeStart;
    }

    public void setBroadcastCodeStart(int broadcastCodeStart) {
        this.broadcastCodeStart = broadcastCodeStart;
    }

    public int getBroadcastCodeLimit() {
        return broadcastCodeLimit;
    }

    public void setBroadcastCodeLimit(int broadcastCodeLimit) {
        this.broadcastCodeLimit = broadcastCodeLimit;
    }

    public int getNotifyBefore() {
        return notifyBefore;
    }

    public void setNotifyBefore(int notifyBefore) {
        this.notifyBefore = notifyBefore;
    }

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        this.notified = notified;
    }
}
