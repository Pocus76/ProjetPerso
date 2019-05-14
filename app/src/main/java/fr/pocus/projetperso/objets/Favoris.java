package fr.pocus.projetperso.objets;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Pocus on 20/03/2019.
 */

public class Favoris
{
    private boolean favoris;
    private Date dateCreated;
    private User userSender;

    public Favoris(boolean favoris, Date dateCreated, User userSender)
    {
        this.favoris = favoris;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
    }

    public Favoris(boolean favoris, User userSender)
    {
        this.favoris = favoris;
        this.userSender = userSender;
    }

    // --- GETTERS ---
    public boolean getFavoris() { return favoris; }

    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    public User getUserSender() { return userSender; }

    // --- SETTERS ---
    public void setFavoris(boolean favoris) { this.favoris = favoris; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
}