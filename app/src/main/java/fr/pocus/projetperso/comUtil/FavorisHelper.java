package fr.pocus.projetperso.comUtil;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.pocus.projetperso.objets.Favoris;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.User;
import fr.pocus.projetperso.utils.FirebaseListener;

/**
 * Created by Pocus on 14/05/2019.
 */

public class FavorisHelper
{
    private static final String COLLECTION_NAME = "favoris";
    private static Collection<FirebaseListener> firebaseListeners = new ArrayList<>();

    public static void addFavorisListener(FirebaseListener listener)
    {
        firebaseListeners.add(listener);
    }

    public static void removeFavorisListener(FirebaseListener listener)
    {
        firebaseListeners.remove(listener);
    }

    protected static void fireFavorisGet(HashMap<String, Boolean> listeFavoris)
    {
        for(FirebaseListener listener : firebaseListeners)
        {
            listener.favorisGet(listeFavoris);
        }
    }

    // --- GET ---

    public static Query getAllFavorisForAMovie(String movie)
    {
        return MovieHelper.getMovieCollection().document(movie).collection(COLLECTION_NAME).orderBy("dateCreated").limit(50);
    }

    public static void getAllFavorisFromAUser()
    {
        final HashMap<String, Boolean> listFavoris = new HashMap<>();
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
                                            Boolean favoris = document.getBoolean("favoris");
                                            listFavoris.put(movieName, favoris);
                                        }
                                    }
                                }
                                fireFavorisGet(listFavoris);
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

    public static void createFavorisForMovie(final boolean bFavoris, final Movie movie, final User userSender)
    {
        // 1 - Create the Message object
        final Favoris favoris = new Favoris(bFavoris, userSender);

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
                            MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).document(document.getId()).update("favoris", true);
                            hasAlreadyRated = true;
                            break;
                        }
                    }
                    if (!hasAlreadyRated)
                    {
                        Map<String, Object> mapMovie = new HashMap<>();
                        mapMovie.put("movie", movie.getTitle());
                        MovieHelper.getMovieCollection().document(movie.getTitle()).set(mapMovie);
                        MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).add(favoris);
                    }
                }
            }
        });
    }

    public static void deleteFavorisForMovie(final Movie movie, final User userSender)
    {
        // 1 - Create the Message object
        final Favoris favoris = new Favoris(false, userSender);

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
                            MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).document(document.getId()).update("favoris", false);
                            hasAlreadyRated = true;
                            break;
                        }
                    }
                    if (!hasAlreadyRated)
                    {
                        Map<String, Object> mapMovie = new HashMap<>();
                        mapMovie.put("movie", movie.getTitle());
                        MovieHelper.getMovieCollection().document(movie.getTitle()).set(mapMovie);
                        MovieHelper.getMovieCollection().document(movie.getTitle()).collection(COLLECTION_NAME).add(favoris);
                    }
                }
            }
        });
    }
}
