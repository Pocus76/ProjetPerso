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
import java.util.List;
import java.util.Map;

import fr.pocus.projetperso.comUtil.ApiConnection;
import fr.pocus.projetperso.comUtil.CommentHelper;
import fr.pocus.projetperso.objets.Comment;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.utils.CommentListener;
import fr.pocus.projetperso.utils.DateUtils;
import fr.pocus.projetperso.utils.GetMovieListener;

/**
 * Created by Pocus on 14/05/2019.
 */
public class FilmsCommentesActivity extends AppCompatActivity implements CommentListener, GetMovieListener
{
    private ListView listViewComments;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films_commentes);

        listViewComments = findViewById(R.id.listview_comments);

        CommentHelper.addCommentListener(this);
        CommentHelper.getAllCommentsForAUser();
    }

    @Override
    public void commentGet(List<Comment> comments) {}

    @Override
    public void commentGet(HashMap<String, Comment> listeComments)
    {
        //Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();

        for(Map.Entry<String, Comment> entry : listeComments.entrySet())
        {
            String nomFilm = entry.getKey();
            Comment comment = entry.getValue();

            HashMap<String, String> map;
            //Création d'une HashMap pour insérer les informations des de notre listView
            map = new HashMap<>();
            map.put("film", nomFilm);
            map.put("date", DateUtils.toString(comment.getDateCreated()));
            map.put("comment", comment.getMessage());

            //enfin on ajoute cette hashMap dans la arrayList
            listItem.add(map);
        }
        //Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_item,
                new String[] {"film", "date", "comment"}, new int[] {R.id.nom_item, R.id.date_item, R.id.description_item});

        //On attribue à notre listView l'adapter que l'on vient de créer
        listViewComments.setAdapter(mSchedule);

        listViewComments.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id)
            {
                ApiConnection.addRatingListener(FilmsCommentesActivity.this);
                //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                HashMap<String, String> map = (HashMap<String, String>) listViewComments.getItemAtPosition(position);
                new ApiConnection().execute(map.get("film"));
                pd = new ProgressDialog(FilmsCommentesActivity.this);
                pd.setMessage("Veuillez patienter");
                pd.setCancelable(false);
                pd.show();
            }
        });
    }

    @Override
    public void movieGet(Movie movie)
    {
        pd.dismiss();
        ApiConnection.removeRatingListener(FilmsCommentesActivity.this);
        Intent intent = new Intent(FilmsCommentesActivity.this, MovieDetailActivity.class);
        intent.putExtra("detail", movie);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop()
    {
        CommentHelper.removeCommentListener(FilmsCommentesActivity.this);
        super.onStop();
    }
}
