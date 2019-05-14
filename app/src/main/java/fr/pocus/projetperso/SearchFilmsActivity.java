package fr.pocus.projetperso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.pocus.projetperso.comUtil.ApiConnection;
import fr.pocus.projetperso.comUtil.CommentHelper;
import fr.pocus.projetperso.comUtil.NetworkUtils;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.utils.GetMovieListener;

/**
 * Created by Pocus on 14/05/2019.
 */

public class SearchFilmsActivity extends AppCompatActivity implements GetMovieListener
{
    private static final String TAG = "SEARCH_FILMS";

    private SearchView search;
    private ListView listFilmsSearch;
    private ProgressBar mProgressBar;
    ArrayList<Movie> listMovies;
    private String textSearch;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_films);

        search = findViewById(R.id.search);
        listFilmsSearch = findViewById(R.id.listview_search);
        mProgressBar = findViewById(R.id.indeterminateBar);

        search.setIconified(false);
        search.requestFocus();

        mProgressBar.setVisibility(View.GONE); //Hide Progressbar by Default

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Log.d(TAG, "onQueryTextChange: "+newText);
                textSearch = newText;
                new CommunicationApi().execute();
                return false;
            }
        });
        listFilmsSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String titre = (String) listFilmsSearch.getItemAtPosition(position);
                ApiConnection.addRatingListener(SearchFilmsActivity.this);
                new ApiConnection().execute(titre);
                pd = new ProgressDialog(SearchFilmsActivity.this);
                pd.setMessage("Veuillez patienter");
                pd.setCancelable(false);
                pd.show();

            }
        });
    }

    private void refreshList(List<Movie> movies)
    {
        List<String> listeTitresFilms = new ArrayList<>();
        for (Movie movie : movies)
        {
            listeTitresFilms.add(movie.getTitle());
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchFilmsActivity.this,
                R.layout.simple_list_item_1, listeTitresFilms);
        listFilmsSearch.setAdapter(adapter);
    }

    @Override
    public void movieGet(Movie movie)
    {
        pd.dismiss();
        ApiConnection.removeRatingListener(SearchFilmsActivity.this);
        Intent intent = new Intent(SearchFilmsActivity.this, MovieDetailActivity.class);
        intent.putExtra("detail", movie);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop()
    {
        ApiConnection.removeRatingListener(SearchFilmsActivity.this);
        super.onStop();
    }

    private class CommunicationApi extends AsyncTask<String, Void, String>
    {
        private static final String TAG = "COM_API";
        private static final String API_URL = "https://api.themoviedb.org/3/search/";
        private static final String API_KEY = "647935883838e1fbb28ad4acf3226909";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String URL = API_URL+"movie?api_key="+API_KEY+"&language=fr&query="+textSearch+"&include_adult=false";

            listMovies = new ArrayList<>();

            try
            {
                if(NetworkUtils.networkStatus(SearchFilmsActivity.this))
                {
                    listMovies = NetworkUtils.fetchData(URL); //Get movies
                }
                else
                {
                    Toast.makeText(SearchFilmsActivity.this,"Vous n'êtes pas connecté à internet",Toast.LENGTH_LONG).show();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
            runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    Log.d(TAG, "run: "+listMovies);
                    refreshList(listMovies);
                }
            });
        }
    }
}
