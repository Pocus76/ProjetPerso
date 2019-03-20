package fr.pocus.projetperso.comUtil;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.objets.User;

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

    public static Task<DocumentReference> createRateForMovie(float rate, String movie, User userSender)
    {
        // 1 - Create the Message object
        Rating rating = new Rating(rate, userSender);

        // 2 - Store Message to Firestore
        return MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).add(rating);
    }
}
