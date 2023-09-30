package com.example.tcc.network.entities;

import java.util.Date;

public class Post {
    private Long id;
    private Integer qntdDenuncia;
    private String comentario;
    private String cidade;
    private String estado;
    private Date dataPost;
    private Integer cep;

    public Post() {
    }

    public Post(Long id, Integer qntdDenuncia, String comentario, String cidade, String estado, Date dataPost, Integer cep) {
        this.id = id;
        this.qntdDenuncia = qntdDenuncia;
        this.comentario = comentario;
        this.cidade = cidade;
        this.estado = estado;
        this.dataPost = dataPost;
        this.cep = cep;
    }

    public Post(Integer qntdDenuncia, String comentario, String cidade, String estado, Date dataPost, Integer cep) {
        this.qntdDenuncia = qntdDenuncia;
        this.comentario = comentario;
        this.cidade = cidade;
        this.estado = estado;
        this.dataPost = dataPost;
        this.cep = cep;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQntdDenuncia() {
        return qntdDenuncia;
    }

    public void setQntdDenuncia(Integer qntdDenuncia) {
        this.qntdDenuncia = qntdDenuncia;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getDataPost() {
        return dataPost;
    }

    public void setDataPost(Date dataPost) {
        this.dataPost = dataPost;
    }

    public Integer getCep() {
        return cep;
    }

    public void setCep(Integer cep) {
        this.cep = cep;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", qntdDenuncia=" + qntdDenuncia +
                ", comentario='" + comentario + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", dataPost=" + dataPost +
                ", cep=" + cep +
                '}';
    }
}
