package com.example.tcc.network.entities;

import java.io.Serializable;

public class DetalhesBusca implements Serializable {

    private boolean quarto;
    private boolean garagem;
    private Integer moradores;
    private boolean pets;
    private String generoMoradia;
    private Double valorAluguelMinimo;
    private Double valorAluguelMaximo;
    private boolean tipoResidencia;

    public DetalhesBusca() {
    }

    public DetalhesBusca(boolean quarto, boolean garagem, Integer moradores, boolean pets, String generoMoradia, Double valorAluguelMinimo, Double valorAluguelMaximo, boolean tipoResidencia) {
        this.quarto = quarto;
        this.garagem = garagem;
        this.moradores = moradores;
        this.pets = pets;
        this.generoMoradia = generoMoradia;
        this.valorAluguelMinimo = valorAluguelMinimo;
        this.valorAluguelMaximo = valorAluguelMaximo;
        this.tipoResidencia = tipoResidencia;
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

    public Double getValorAluguelMinimo() {
        return valorAluguelMinimo;
    }

    public void setValorAluguelMinimo(Double valorAluguelMinimo) {
        this.valorAluguelMinimo = valorAluguelMinimo;
    }

    public Double getValorAluguelMaximo() {
        return valorAluguelMaximo;
    }

    public void setValorAluguelMaximo(Double valorAluguelMaximo) {
        this.valorAluguelMaximo = valorAluguelMaximo;
    }

    public boolean isTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(boolean tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    @Override
    public String toString() {
        return "DetalhesBusca{" +
                "quarto=" + quarto +
                ", garagem=" + garagem +
                ", moradores=" + moradores +
                ", pets=" + pets +
                ", generoMoradia='" + generoMoradia + '\'' +
                ", valorAluguelInicial=" + valorAluguelMinimo +
                ", valorAluguelFinal=" + valorAluguelMaximo +
                ", tipoResidencia=" + tipoResidencia +
                '}';
    }
}
