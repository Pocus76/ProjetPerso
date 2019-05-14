package fr.pocus.projetperso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.pocus.projetperso.comUtil.ApiConnection;
import fr.pocus.projetperso.comUtil.FavorisHelper;
import fr.pocus.projetperso.comUtil.RatingHelper;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.utils.DateUtils;
import fr.pocus.projetperso.utils.FirebaseListener;
import fr.pocus.projetperso.utils.GetMovieListener;

/**
 * Created by Pocus on 14/05/2019.
 */

public class FilmsFavorisActivity extends AppCompatActivity implements FirebaseListener, GetMovieListener
{
    private ListView listViewFavoris;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films_favoris);

        listViewFavoris = findViewById(R.id.listview_favoris);

        FavorisHelper.addFavorisListener(this);
        FavorisHelper.getAllFavorisFromAUser();
    }

    @Override
    public void movieGet(Movie movie)
    {
        pd.dismiss();
        ApiConnection.removeRatingListener(FilmsFavorisActivity.this);
        Intent intent = new Intent(FilmsFavorisActivity.this, MovieDetailActivity.class);
        intent.putExtra("detail", movie);
        startActivity(intent);
        finish();
    }

    @Override
    public void ratingGet(HashMap<String, Rating> listeNotes) {}

    @Override
    public void favorisGet(HashMap<String, Boolean> listeFavoris)
    {
        //Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<String> listItem = new ArrayList<>();

        for(Map.Entry<String, Boolean> entry : listeFavoris.entrySet())
        {
            String nomFilm = entry.getKey();
            boolean favoris = entry.getValue();

            //enfin on ajoute cette hashMap dans la arrayList
            if (favoris) listItem.add(nomFilm);
        }
        //Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(FilmsFavorisActivity.this, R.layout.simple_list_item_1, listItem);
        listViewFavoris.setAdapter(adapter);

        listViewFavoris.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id)
            {
                ApiConnection.addRatingListener(FilmsFavorisActivity.this);
                //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                String nomFilm = (String) listViewFavoris.getItemAtPosition(position);
                new ApiConnection().execute(nomFilm);
                pd = new ProgressDialog(FilmsFavorisActivity.this);
                pd.setMessage("Veuillez patienter");
                pd.setCancelable(false);
                pd.show();
            }
        });
    }

    @Override
    protected void onStop()
    {
        FavorisHelper.removeFavorisListener(FilmsFavorisActivity.this);
        super.onStop();
    }
}
