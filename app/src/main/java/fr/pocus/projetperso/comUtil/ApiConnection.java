package fr.pocus.projetperso.comUtil;

import android.os.AsyncTask;
import android.util.Log;

import com.google.protobuf.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.utils.GetMovieListener;

/**
 * Created by Pocus on 25/01/2019.
 */

public class ApiConnection extends AsyncTask<String, Void, Movie>
{
    private static final String TAG = "COM_API";
    private static final String API_URL = "http://api.themoviedb.org/3/discover/";
    private static final String API_KEY = "647935883838e1fbb28ad4acf3226909";


    private static Collection<GetMovieListener> firebaseListeners = new ArrayList<>();

    public static void addRatingListener(GetMovieListener listener)
    {
        firebaseListeners.add(listener);
    }

    public static void removeRatingListener(GetMovieListener listener)
    {
        firebaseListeners.remove(listener);
    }

    protected static void fireRatingGet(Movie movie)
    {
        for(GetMovieListener listener : firebaseListeners)
        {
            listener.movieGet(movie);
        }
    }

    @Override
    protected Movie doInBackground(String... params)
    {
        Movie movie = null;
        try
        {
            String nomPourAPI = params[0].replace(" ", "+");
            String movieByNameURL = "https://api.themoviedb.org/3/search/movie?language=fr&api_key="+API_KEY+"&query="+nomPourAPI;
            movie = NetworkUtils.fetchData(movieByNameURL).get(0);
            Log.d(TAG, "getFilmParNom "+movie.getTitle());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return movie;
    }

    @Override
    protected void onPostExecute(Movie movie)
    {
        super.onPostExecute(movie);
        fireRatingGet(movie);
    }
}
