package fr.pocus.projetperso.comUtil;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import fr.pocus.projetperso.objets.Comment;
import fr.pocus.projetperso.objets.User;

/**
 * Created by Pocus on 25/01/2019.
 */

public class CommentHelper
{
    private static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static Query getAllCommentsForMovie(String movie)
    {
        return MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).orderBy("dateCreated").limit(50);
    }

    public static Task<DocumentReference> createMessageForChat(String textMessage, String movie, User userSender)
    {
        // 1 - Create the Message object
        Comment message = new Comment(textMessage, userSender);

        // 2 - Store Message to Firestore
        return MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).add(message);
    }
}
