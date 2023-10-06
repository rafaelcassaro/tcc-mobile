package com.example.tcc.network.entities;

public class DetalhesMoradia {

    private Long id;
    private String quarto;
    private String garagem;
    private Integer moradores;
    private String pets;
    private String generoMoradia;

    public DetalhesMoradia() {
    }

    public DetalhesMoradia(Long id, String quarto, String garagem, Integer moradores, String pets, String generoMoradia) {
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

    public String getQuarto() {
        return quarto;
    }

    public void setQuarto(String quarto) {
        this.quarto = quarto;
    }

    public String getGaragem() {
        return garagem;
    }

    public void setGaragem(String garagem) {
        this.garagem = garagem;
    }

    public Integer getMoradores() {
        return moradores;
    }

    public void setMoradores(Integer moradores) {
        this.moradores = moradores;
    }

    public String getPets() {
        return pets;
    }

    public void setPets(String pets) {
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
