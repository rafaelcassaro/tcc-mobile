package com.example.tcc.network.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListaPosts implements Serializable {

    private List<Post> db = new ArrayList<>();

    public ListaPosts() {
    }

    public ListaPosts(List<Post> db) {
        this.db.addAll(db);
    }

    public List<Post> getDb() {
        return db;
    }

    public void setDb(List<Post> db) {
        this.db.addAll(db);
    }
}
