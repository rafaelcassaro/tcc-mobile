package com.example.tcc.network.entities;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Post implements Serializable {
    private Long id;
    private Integer qntdDenuncia;
    private String comentario;
    private String cidade;
    private String estado;
    private String dataPost;
    private String cep;
    //protected SimpleDateFormat x = new SimpleDateFormat("dd/MM/yyyy");

    private Usuario usuario;

    private PostMoradia postMoradia;

    public Post() {
    }

    public Post(Long id, Integer qntdDenuncia, String comentario, String cidade, String estado, String dataPost, String cep) {
        this.id = id;
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

    public String getDataPost() {
        return dataPost;
    }

    /*public void setDataPost(Date dataPost) {
        this.dataPost = x.format(dataPost);
    }*/

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public PostMoradia getPostMoradia() {
        return postMoradia;
    }

    public void setPostMoradia(PostMoradia postMoradia) {
        this.postMoradia = postMoradia;
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
                ", cep='" + cep + '\'' +

                ", usuario=" + usuario +
                ", postMoradia=" + postMoradia +
                '}';
    }
}
