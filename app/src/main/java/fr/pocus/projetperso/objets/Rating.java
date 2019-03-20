package fr.pocus.projetperso.objets;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Pocus on 20/03/2019.
 */

public class Rating
{
    private float rate;
    private Date dateCreated;
    private User userSender;

    public Rating() { }

    public Rating(float rate, User userSender)
    {
        this.rate = rate;
        this.userSender = userSender;
    }

    // --- GETTERS ---
    public float getRating() { return rate; }

    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    public User getUserSender() { return userSender; }

    // --- SETTERS ---
    public void setMessage(float rate) { this.rate = rate; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
}