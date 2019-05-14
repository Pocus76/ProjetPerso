package fr.pocus.projetperso;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pocus.projetperso.comUtil.CommentHelper;
import fr.pocus.projetperso.comUtil.FavorisHelper;
import fr.pocus.projetperso.comUtil.FirebaseGestion;
import fr.pocus.projetperso.comUtil.RatingHelper;
import fr.pocus.projetperso.comUtil.UserHelper;
import fr.pocus.projetperso.objets.Comment;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.objets.User;
import fr.pocus.projetperso.utils.CommentAdapter;
import fr.pocus.projetperso.utils.CommentListener;
import fr.pocus.projetperso.utils.DateUtils;
import fr.pocus.projetperso.utils.FirebaseListener;

/**
 * Created by Pocus on 24/01/2019.
 */

public class MovieDetailActivity extends AppCompatActivity implements CommentListener, FirebaseListener
{
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    public static final String BASE_URL ="https://image.tmdb.org/t/p/w185";

    private TextView txtTitre;
    private ImageView imgFilm;
    private ImageView imgBookmark;
    private RatingBar ratingBar;
    private TextView txtVote;
    private TextView txtDescription;
    private LinearLayout layoutAddComment;
    private TextView txtConnecte;
    private EditText txtComment;
    private ImageButton btnSendComment;
    private ListView listComments;
    private Dialog rankDialog;

    private Movie mov_intent;
    private boolean isFavoris = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        txtTitre = (TextView) findViewById(R.id.titre_film);
        imgFilm = (ImageView) findViewById(R.id.imageViewFilm);
        imgBookmark = findViewById(R.id.imageViewBookmark);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        txtVote = (TextView) findViewById(R.id.vote_film);
        txtDescription = (TextView) findViewById(R.id.description_film);
        txtComment = (EditText) findViewById(R.id.txt_comment);
        btnSendComment = (ImageButton) findViewById(R.id.btn_submit_comment);
        layoutAddComment = (LinearLayout) findViewById(R.id.layout_add_comment);
        txtConnecte = (TextView) findViewById(R.id.txt_connecte);
        listComments = (ListView) findViewById(R.id.listview_comments);

        Intent intent = getIntent();
        this.mov_intent = (Movie) intent.getSerializableExtra("detail");

        //load data into the ImageView using Picasso
        Picasso.get().load(BASE_URL + mov_intent.getPosterPath())
                .placeholder(R.drawable.image_placeholder)
                .into(imgFilm);
        txtTitre.setText(mov_intent.getTitle()+" ("+ DateUtils.formatDate(mov_intent.getReleaseDate())+")");
        ratingBar.setNumStars(10);
        ratingBar.setMax(10);
        ratingBar.setRating((float) mov_intent.getVoteAverage());
        txtVote.setText(mov_intent.getVoteAverage()+"/10 ("+mov_intent.getVoteCount()+" notes)");
        txtDescription.setText(mov_intent.getOverview());

        ratingBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                rankDialog = new Dialog(MovieDetailActivity.this, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rank_dialog);
                rankDialog.setCancelable(true);
                ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
                ratingBar.setRating(2);

                TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
                text.setText("Notez ce film !");

                Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
                updateButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        RatingHelper.createRateForMovie(ratingBar.getRating(), mov_intent, FirebaseGestion.modelCurrentUser);
                        Toast.makeText(getApplicationContext(), "Vous avez noté ce film "+(int)ratingBar.getRating()+"/10", Toast.LENGTH_SHORT).show();
                        rankDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                rankDialog.show();
                return false;
            }
        });

        FavorisHelper.addFavorisListener(this);
        if (FirebaseGestion.modelCurrentUser!=null) FavorisHelper.getAllFavorisFromAUser();

        imgBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!isFavoris)
                {
                    FavorisHelper.createFavorisForMovie(true, mov_intent, FirebaseGestion.modelCurrentUser);
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark));
                    Toast.makeText(getApplicationContext(), "Ce film a été ajouté à vos favoris", Toast.LENGTH_SHORT).show();
                    isFavoris = true;
                }
                else
                {
                    FavorisHelper.deleteFavorisForMovie(mov_intent, FirebaseGestion.modelCurrentUser);
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_black));
                    Toast.makeText(getApplicationContext(), "Ce film a été retiré de vos favoris", Toast.LENGTH_SHORT).show();
                    isFavoris = false;
                }
            }
        });

        if (FirebaseGestion.modelCurrentUser==null)
        {
            layoutAddComment.setVisibility(View.GONE);
            imgBookmark.setVisibility(View.GONE);
            txtConnecte.setVisibility(View.VISIBLE);
            ratingBar.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    Toast.makeText(getApplicationContext(), "Vous devez être connecté pour noter un film", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }


        btnSendComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!TextUtils.isEmpty(txtComment.getText()))
                {
                    // 2 - Create a new Message to Firestore
                    String commentContent = txtComment.getText().toString().replace("\n", " ");
                    CommentHelper.createMessageForChat(commentContent, mov_intent, FirebaseGestion.modelCurrentUser);

                    // 3 - Reset text field
                    txtComment.setText("");
                }
            }
        });

        CommentHelper.addCommentListener(this);

        CommentHelper.getAllCommentsForMoviepopo(mov_intent);
    }

    @Override
    public void commentGet(List<Comment> comments)
    {
        //Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();

        for(Comment comment : comments)
        {
            HashMap<String, String> map;
            //Création d'une HashMap pour insérer les informations des de notre listView
            map = new HashMap<>();
            map.put("user", comment.getUserSender().getUsername());
            if (comment.getDateCreated()!=null)
            {
                map.put("date", DateUtils.toString(comment.getDateCreated()));
            }
            else map.put("date", null);
            map.put("commentaire", comment.getMessage());

            //enfin on ajoute cette hashMap dans la arrayList
            listItem.add(map);
        }
        Log.d("TOTOTOTTOo", "onComplete: "+listItem.size());
        //Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_item,
                new String[] {"user", "date", "commentaire"}, new int[] {R.id.nom_item, R.id.date_item, R.id.description_item});

        //On attribue à notre listView l'adapter que l'on vient de créer
        listComments.setAdapter(mSchedule);
        setListViewHeightBasedOnChildren(listComments);
    }

    @Override
    public void commentGet(HashMap<String, Comment> listeComments) {}

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void ratingGet(HashMap<String, Rating> listeNotes) {}

    @Override
    public void favorisGet(HashMap<String, Boolean> listeFavoris)
    {
        for(Map.Entry<String, Boolean> entry : listeFavoris.entrySet())
        {
            String nomFilm = entry.getKey();
            boolean favoris = entry.getValue();
            if (nomFilm.equals(mov_intent.getTitle())&&favoris)
            {
                imgBookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark));
                isFavoris = true;
            }
        }
    }
}
