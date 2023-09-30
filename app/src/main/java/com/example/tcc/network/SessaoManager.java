package com.example.tcc.network;

import com.example.tcc.network.entities.Usuario;

public class SessaoManager {
    private static SessaoManager instance;
    private boolean loggedIn = false;
    private Usuario user = new Usuario();

    // Construtor privado para garantir que apenas uma instância seja criada
    private SessaoManager() {}

    // Método para obter a única instância da classe
    public static synchronized SessaoManager getInstance() {
        if (instance == null) {
            instance = new SessaoManager();
        }
        return instance;
    }

    // Métodos para definir e verificar o estado de login
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
}
