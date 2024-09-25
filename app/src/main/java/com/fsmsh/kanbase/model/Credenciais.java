package com.fsmsh.kanbase.model;

public class Credenciais {

    public static final int TYPE_REGISTER = 0;
    public static final int TYPE_LOGIN = 1;

    int tipo;
    String nome;
    String email;
    String senha;


    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
