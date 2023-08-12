package me.armandosalazar.mechanicsforapp.models;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private String password;
    private boolean registered;
    private String username;
    private String name;
    public User() {
        this.email = "none";
        this.password = "none";
        this.registered = false;
    }

    public User(String  email, String password, boolean registered) {
        this.email = email;
        this.password = password;
        this.registered = registered;
        this.name = name;
        this.username = username;
    }

    public User(String name, String username, String  email, String password, boolean registered) {
        this.email = email;
        this.password = password;
        this.registered = registered;
        this.name = name;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
