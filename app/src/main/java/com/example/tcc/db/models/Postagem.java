package com.example.tcc.db.models;

import java.io.Serializable;

public class Postagem implements Serializable {

    private String nome;
    private String cidade;
    private String comentario;
    private String celular;


    public Postagem(String nome, String cidade, String comentario, String celular) {
        this.nome = nome;
        this.cidade = cidade;
        this.comentario = comentario;
        this.celular = celular;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}
