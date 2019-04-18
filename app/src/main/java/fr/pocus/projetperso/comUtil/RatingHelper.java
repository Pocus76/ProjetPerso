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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.objets.User;

import static android.R.attr.rating;

/**
 * Created by Pocus on 20/03/2019.
 */

public class RatingHelper
{
    private static final String COLLECTION_NAME = "ratings";

    // --- GET ---

    public static Query getAllRatingsForAMovie(String movie)
    {
        return MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).orderBy("dateCreated").limit(50);
    }

    public static HashMap<String, Rating> getAllRatingsFromAUser()
    {
        Log.d("TOTOTOTOTO", "On est la");
        final HashMap<String, Rating> listRating = new HashMap<>();
        FirebaseFirestore.getInstance().collection("movies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    Log.d("TOTOTOTOTO", "On est la2");
                    Log.d("TOTOTOTOTO", "size "+task.getResult().size());
                    for (QueryDocumentSnapshot doc : task.getResult())
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
                                            Log.d("Rating", "onComplete: "+document.toObject(Rating.class));
                                            listRating.put(movieName, document.toObject(Rating.class));
                                        }
                                    }
                                }
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
        return listRating;
    }

    public static void createRateForMovie(final float rate, final String movie, final User userSender)
    {
        // 1 - Create the Message object
        final Rating rating = new Rating(rate, userSender);

        // 2 - Store Message to Firestore
        MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
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
                            MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).document(document.getId()).update("rating", rate);
                            hasAlreadyRated = true;
                            break;
                        }
                    }
                    if (!hasAlreadyRated)
                    {
                        MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).add(rating);
                    }
                }
            }
        });
    }
}
