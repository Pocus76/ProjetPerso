package fr.pocus.projetperso.objets;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Pocus on 25/01/2019.
 */

public class Comment
{
    private String message;
    private Date dateCreated;
    private User userSender;
    private String movieName;

    public Comment() {}

    public Comment(String message, Date dateCreated, User userSender)
    {
        this.message = message;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
    }

    public Comment(String message, User userSender)
    {
        this.message = message;
        this.userSender = userSender;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }

    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    public User getUserSender() { return userSender; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}
