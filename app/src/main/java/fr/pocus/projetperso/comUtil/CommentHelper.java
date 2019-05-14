package fr.pocus.projetperso.comUtil;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pocus.projetperso.objets.Comment;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.User;
import fr.pocus.projetperso.utils.CommentListener;

/**
 * Created by Pocus on 25/01/2019.
 */

public class CommentHelper
{
    private static final String COLLECTION_NAME = "messages";
    private static Collection<CommentListener> firebaseListeners = new ArrayList<>();

    public static void addCommentListener(CommentListener listener)
    {
        firebaseListeners.add(listener);
    }

    public static void removeCommentListener(CommentListener listener)
    {
        firebaseListeners.remove(listener);
    }

    protected static void fireCommentGet(List<Comment> listeComments)
    {
        for(CommentListener listener : firebaseListeners)
        {
            listener.commentGet(listeComments);
        }
    }

    protected static void fireCommentGet(HashMap<String, Comment> listeComments)
    {
        for(CommentListener listener : firebaseListeners)
        {
            listener.commentGet(listeComments);
        }
    }

    // --- GET ---

    public static Query getAllCommentsForMovie(Movie movie)
    {
        return MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).orderBy("dateCreated").limit(50);
    }

    public static void getAllCommentsForMoviepopo(final Movie movie)
    {
        final List<Comment> listeCommentaires = new ArrayList<>();
        MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).orderBy("dateCreated").limit(50).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (final QueryDocumentSnapshot doc : task.getResult())
                    {
                        HashMap mapUserSender = (HashMap) doc.getData().get("userSender");
                        User userSender = new User(mapUserSender.get("uid").toString(), mapUserSender.get("username").toString());
                        Comment comment = new Comment(doc.getData().get("message").toString(), doc.getDate("dateCreated"), userSender);
                        comment.setMovieName(movie.getTitle());
                        listeCommentaires.add(comment);
                    }
                }
                Collections.reverse(listeCommentaires);
                CommentHelper.fireCommentGet(listeCommentaires);
            }
        });
    }

    public static void getAllCommentsForAUser()
    {
        final HashMap<String, Comment> listComments = new HashMap<>();
        MovieHelper.getMovieCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (final QueryDocumentSnapshot doc : task.getResult())
                    {
                        final String movieName = doc.getId();
                        MovieHelper.getMovieCollection().document(movieName).collection(COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if (task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        Map<String, Object> listDatas = document.getData();
                                        Map<String, Object> listDataUser = (Map<String, Object>) listDatas.get("userSender");
                                        if (listDataUser.get("username").equals(FirebaseGestion.modelCurrentUser.getUsername()))
                                        {
                                            HashMap mapUserSender = (HashMap) document.getData().get("userSender");
                                            User userSender = new User(mapUserSender.get("uid").toString(), mapUserSender.get("username").toString());
                                            Comment comment = new Comment(document.getString("message"), document.getDate("dateCreated"), userSender);
                                            listComments.put(movieName, comment);
                                        }
                                    }
                                }
                                fireCommentGet(listComments);
                            }
                        });
                    }
                }
                else
                {
                    Log.d("TOTOTOTOTO", "onComplete: FAIL");
                }
            }
        });
    }

    public static void createMessageForChat(String textMessage, Movie movie, User userSender)
    {
        // 1 - Create the Message object
        Comment message = new Comment(textMessage, userSender);

        // 2 - Store Message to Firestore
        Map<String, Object> mapMovie = new HashMap<>();
        mapMovie.put("movie", movie.getTitle());

        MovieHelper.getMovieCollection().document(movie.getTitle()).set(mapMovie);
        MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).add(message);

        CommentHelper.getAllCommentsForMoviepopo(movie);
    }
}
