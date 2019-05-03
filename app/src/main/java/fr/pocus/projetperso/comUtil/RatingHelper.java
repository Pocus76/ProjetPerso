package fr.pocus.projetperso.comUtil;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.objets.User;
import fr.pocus.projetperso.utils.FirebaseListener;

import static android.R.attr.rating;

/**
 * Created by Pocus on 20/03/2019.
 */

public class RatingHelper
{
    private static final String COLLECTION_NAME = "ratings";
    private static Collection<FirebaseListener> firebaseListeners = new ArrayList<>();

    public static void addRatingListener(FirebaseListener listener)
    {
        firebaseListeners.add(listener);
    }

    public static void removeRatingListener(FirebaseListener listener)
    {
        firebaseListeners.remove(listener);
    }

    protected static void fireRatingGet(HashMap<String, Rating> listeRatings)
    {
        for(FirebaseListener listener : firebaseListeners)
        {
            listener.ratingGet(listeRatings);
        }
    }

    // --- GET ---

    public static Query getAllRatingsForAMovie(String movie)
    {
        return MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).orderBy("dateCreated").limit(50);
    }

    public static void getAllRatingsFromAUser()
    {
        final HashMap<String, Rating> listRating = new HashMap<>();
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
                                            Rating rating = new Rating(((Double) document.getData().get("rating")).floatValue(), document.getDate("dateCreated"), userSender);
                                            listRating.put(movieName, rating);
                                        }
                                    }
                                }
                                fireRatingGet(listRating);
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

    public static void createRateForMovie(final float rate, final Movie movie, final User userSender)
    {
        // 1 - Create the Message object
        final Rating rating = new Rating(rate, userSender);

        // 2 - Store Message to Firestore
        MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    boolean hasAlreadyRated = false;
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Map<String, Object> listDatas = document.getData();
                        Map<String, Object> listDataUser = (Map<String, Object>) listDatas.get("userSender");
                        if (listDataUser.get("username").equals(FirebaseGestion.modelCurrentUser.getUsername()))
                        {
                            MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).document(document.getId()).update("rating", rate);
                            hasAlreadyRated = true;
                            break;
                        }
                    }
                    if (!hasAlreadyRated)
                    {
                        Map<String, Object> mapMovie = new HashMap<>();
                        mapMovie.put("movie", movie.getTitle());
                        MovieHelper.getMovieCollection().document(movie.getTitle()).set(mapMovie);
                        MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).add(rating);
                    }
                }
            }
        });
    }
}
