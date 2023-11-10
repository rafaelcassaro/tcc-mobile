package com.example.tcc.network.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostMoradia implements Serializable {

    private Long id;
    private boolean tipoResidencia;
    private String endereco;
    private Integer numCasa;
    private Double valorAluguel;
    private Detalhes detalhes;
    private List<Fotos> fotos;

    public PostMoradia() {
    }

    public PostMoradia(Long id, boolean tipoResidencia, String endereco, Integer numCasa, Double valorAluguel, Detalhes detalhes) {
        this.id = id;
        this.tipoResidencia = tipoResidencia;
        this.endereco = endereco;
        this.numCasa = numCasa;
        this.valorAluguel = valorAluguel;
        this.detalhes = detalhes;
        fotos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(boolean tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getNumCasa() {
        return numCasa;
    }

    public void setNumCasa(Integer numCasa) {
        this.numCasa = numCasa;
    }

    public Double getValorAluguel() {
        return valorAluguel;
    }

    public void setValorAluguel(Double valorAluguel) {
        this.valorAluguel = valorAluguel;
    }

    public Detalhes getDetalhesMoradia() {
        return detalhes;
    }

    public void setDetalhesMoradia(Detalhes detalhes) {
        this.detalhes = detalhes;
    }

    public Detalhes getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(Detalhes detalhes) {
        this.detalhes = detalhes;
    }

    public List<Fotos> getFotos() {
        return fotos;
    }

    public void setFotos(List<Fotos> fotos) {
        this.fotos = fotos;
    }
}
