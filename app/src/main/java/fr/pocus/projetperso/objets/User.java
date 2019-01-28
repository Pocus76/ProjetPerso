package fr.pocus.projetperso.objets;

/**
 * Created by Pocus on 25/01/2019.
 */

public class User
{
    private String uid;
    private String username;
    private boolean isModo;
    private boolean isAdmin;

    public User() {}

    public User(String uid, String username)
    {
        this.uid = uid;
        this.username = username;
        this.isModo = false;
        this.isAdmin = false;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isModo() {
        return isModo;
    }

    public void setModo(boolean modo) {
        isModo = modo;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}