package com.example.tcc.network.entities;

import java.io.Serializable;

public class Fotos implements Serializable {

    private Long id;
    private String nomeFoto;
    private String caminhoImagem;

    public Fotos() {
    }

    public Fotos(Long id, String nomeFoto, String caminhoImagem) {
        this.id = id;
        this.nomeFoto = nomeFoto;
        this.caminhoImagem = caminhoImagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFoto() {
        return nomeFoto;
    }

    public void setNomeFoto(String nomeFoto) {
        this.nomeFoto = nomeFoto;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    @Override
    public String toString() {
        return "Fotos{" +
                "id=" + id +
                ", nomeFoto='" + nomeFoto + '\'' +
                ", caminhoImagem='" + caminhoImagem + '\'' +
                '}';
    }
}
