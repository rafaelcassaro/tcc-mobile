package com.example.tcc.network.entities;

import java.io.Serializable;

public class Detalhes implements Serializable {

    private Long id;
    private boolean quarto;
    private boolean garagem;
    private Integer moradores;
    private boolean pets;
    private String generoMoradia;

    public Detalhes() {
    }

    public Detalhes(Long id, boolean quarto, boolean garagem, Integer moradores, boolean pets, String generoMoradia) {
        this.id = id;
        this.quarto = quarto;
        this.garagem = garagem;
        this.moradores = moradores;
        this.pets = pets;
        this.generoMoradia = generoMoradia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isQuarto() {
        return quarto;
    }

    public void setQuarto(boolean quarto) {
        this.quarto = quarto;
    }

    public boolean isGaragem() {
        return garagem;
    }

    public void setGaragem(boolean garagem) {
        this.garagem = garagem;
    }

    public Integer getMoradores() {
        return moradores;
    }

    public void setMoradores(Integer moradores) {
        this.moradores = moradores;
    }

    public boolean isPets() {
        return pets;
    }

    public void setPets(boolean pets) {
        this.pets = pets;
    }

    public String getGeneroMoradia() {
        return generoMoradia;
    }

    public void setGeneroMoradia(String generoMoradia) {
        this.generoMoradia = generoMoradia;
    }

    @Override
    public String toString() {
        return "DetalhesMoradia{" +
                "id=" + id +
                ", quarto='" + quarto + '\'' +
                ", garagem='" + garagem + '\'' +
                ", moradores=" + moradores +
                ", pets='" + pets + '\'' +
                ", generoMoradia='" + generoMoradia + '\'' +
                '}';
    }
}
