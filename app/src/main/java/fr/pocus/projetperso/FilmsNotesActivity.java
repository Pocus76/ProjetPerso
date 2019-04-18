package fr.pocus.projetperso;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;

import fr.pocus.projetperso.comUtil.FirebaseGestion;
import fr.pocus.projetperso.comUtil.RatingHelper;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.utils.RatingAdapter;

/**
 * Created by Pocus on 20/03/2019.
 */

public class FilmsNotesActivity extends AppCompatActivity
{
    private ListView listViewRatings;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films_notes);

        listViewRatings = findViewById(R.id.listview_ratings);

        Log.d("totototototo", "onCreate: SALADE DE FRUIT");

        HashMap<String, Rating> hashMap = RatingHelper.getAllRatingsFromAUser();
        RatingAdapter ratingAdapter = new RatingAdapter(hashMap);
        listViewRatings.setAdapter(ratingAdapter);
    }
}
