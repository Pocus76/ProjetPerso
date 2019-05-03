package fr.pocus.projetperso.comUtil;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Pocus on 25/01/2019.
 */

public class MovieHelper
{
    private static final String COLLECTION_NAME = "movies";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getMovieCollection()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
}
