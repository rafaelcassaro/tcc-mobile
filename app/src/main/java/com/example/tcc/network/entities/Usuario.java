package com.example.tcc.network.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usuario implements Serializable {
    private Long id;
    private String nome;
    private String email;
    private String celular;
    private String senha;
    private String link1;
    private String link2;
    private String link3;

    @SerializedName("token")
    private String token;

    public Usuario(){}

    public Usuario(Long id, String nome, String email, String celular, String senha, String link1, String link2, String link3) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.celular = celular;
        this.senha = senha;
        this.link1 = link1;
        this.link2 = link2;
        this.link3 = link3;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getLink1() {
        return link1;
    }

    public void setLink1(String link1) {
        this.link1 = link1;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public String getLink3() {
        return link3;
    }

    public void setLink3(String link3) {
        this.link3 = link3;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", celular=" + celular +
                ", senha='" + senha + '\'' +
                ", link1='" + link1 + '\'' +
                ", link2='" + link2 + '\'' +
                ", link3='" + link3 + '\'' +
                '}';
    }
}
