package fr.pocus.projetperso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.pocus.projetperso.comUtil.ApiConnection;
import fr.pocus.projetperso.comUtil.RatingHelper;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.utils.DateUtils;
import fr.pocus.projetperso.utils.FirebaseListener;
import fr.pocus.projetperso.utils.GetMovieListener;

/**
 * Created by Pocus on 20/03/2019.
 */

public class FilmsNotesActivity extends AppCompatActivity implements FirebaseListener, GetMovieListener
{
    private ListView listViewRatings;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films_notes);

        listViewRatings = findViewById(R.id.listview_ratings);

        RatingHelper.addRatingListener(this);
        RatingHelper.getAllRatingsFromAUser();
    }

    @Override
    public void ratingGet(HashMap<String, Rating> listeNotes)
    {
        //Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();

        for(Map.Entry<String, Rating> entry : listeNotes.entrySet())
        {
            String nomFilm = entry.getKey();
            Rating note = entry.getValue();

            HashMap<String, String> map;
            //Création d'une HashMap pour insérer les informations des de notre listView
            map = new HashMap<>();
            map.put("film", nomFilm);
            map.put("date", DateUtils.toString(note.getDateCreated()));
            map.put("note", (int)note.getRating()+"/10");

            //enfin on ajoute cette hashMap dans la arrayList
            listItem.add(map);
        }
        //Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_item,
                new String[] {"note", "date", "film"}, new int[] {R.id.nom_item, R.id.date_item, R.id.description_item});

        //On attribue à notre listView l'adapter que l'on vient de créer
        listViewRatings.setAdapter(mSchedule);

        listViewRatings.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id)
            {
                ApiConnection.addRatingListener(FilmsNotesActivity.this);
                //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                HashMap<String, String> map = (HashMap<String, String>) listViewRatings.getItemAtPosition(position);
                new ApiConnection().execute(map.get("film"));
                pd = new ProgressDialog(FilmsNotesActivity.this);
                pd.setMessage("Veuillez patienter");
                pd.setCancelable(false);
                pd.show();
            }
        });
    }

    @Override
    public void favorisGet(HashMap<String, Boolean> listeFavoris) {}

    @Override
    public void movieGet(Movie movie)
    {
        pd.dismiss();
        ApiConnection.removeRatingListener(FilmsNotesActivity.this);
        Intent intent = new Intent(FilmsNotesActivity.this, MovieDetailActivity.class);
        intent.putExtra("detail", movie);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop()
    {
        RatingHelper.removeRatingListener(FilmsNotesActivity.this);
        super.onStop();
    }
}
