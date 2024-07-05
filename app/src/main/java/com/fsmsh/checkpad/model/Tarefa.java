package com.fsmsh.checkpad.model;

public class Tarefa {
    private int id;
    private String tarefaNome;
    private String descricao;
    private int progresso;
    private String dateStart;
    private String timeStart;
    private String dateLimit;
    private String timeLimit;
    private String categoria;
    private int prioridade;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
