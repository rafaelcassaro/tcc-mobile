package com.example.tcc.db.models;

import android.widget.TextView;

import java.io.Serializable;

public class Moradias implements Serializable {

    private String tipoMoradia;
    private String cidade;
    private String rua;
    private int numCasa;
    private String moradores;
    private String quarto;
    private String garagem;
    private String pet;
    private int valor;

    public Moradias(){}

    public Moradias(String tipoMoradia, String cidade, String rua, int numCasa, String moradores, String quarto, String garagem, String pet, int valor) {
        this.tipoMoradia = tipoMoradia;
        this.cidade = cidade;
        this.rua = rua;
        this.numCasa = numCasa;
        this.moradores = moradores;
        this.quarto = quarto;
        this.garagem = garagem;
        this.pet = pet;
        this.valor = valor;
    }

    public String getTipoMoradia() {
        return tipoMoradia;
    }

    public void setTipoMoradia(String tipoMoradia) {
        this.tipoMoradia = tipoMoradia;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public int getNumCasa() {
        return numCasa;
    }

    public void setNumCasa(int numCasa) {
        this.numCasa = numCasa;
    }

    public String getMoradores() {
        return moradores;
    }

    public void setMoradores(String moradores) {
        this.moradores = moradores;
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

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
