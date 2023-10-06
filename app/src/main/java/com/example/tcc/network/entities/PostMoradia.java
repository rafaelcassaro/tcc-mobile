package com.example.tcc.network.entities;

import java.io.Serializable;

public class PostMoradia implements Serializable {

    private Long id;
    private String tipoResidencia;
    private String endereco;
    private Integer numCasa;
    private Double valorAluguel;
    private Detalhes detalhes;

    public PostMoradia() {
    }

    public PostMoradia(Long id, String tipoResidencia, String endereco, Integer numCasa, Double valorAluguel) {
        this.id = id;
        this.tipoResidencia = tipoResidencia;
        this.endereco = endereco;
        this.numCasa = numCasa;
        this.valorAluguel = valorAluguel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(String tipoResidencia) {
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

}
