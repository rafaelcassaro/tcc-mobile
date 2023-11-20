package com.example.tcc.network.entities;

public class Feedback {

    private String comentario;

    public Feedback() {
    }

    public Feedback(String comentario) {
        this.comentario = comentario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "comentario='" + comentario + '\'' +
                '}';
    }
}
