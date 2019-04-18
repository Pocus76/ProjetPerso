package fr.pocus.projetperso;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import fr.pocus.projetperso.comUtil.CommentHelper;
import fr.pocus.projetperso.comUtil.FirebaseGestion;
import fr.pocus.projetperso.comUtil.RatingHelper;
import fr.pocus.projetperso.comUtil.UserHelper;
import fr.pocus.projetperso.objets.Comment;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.Rating;
import fr.pocus.projetperso.objets.User;
import fr.pocus.projetperso.utils.CommentAdapter;
import fr.pocus.projetperso.utils.DateUtils;

/**
 * Created by Pocus on 24/01/2019.
 */

public class MovieDetailActivity extends AppCompatActivity implements CommentAdapter.Listener
{
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    public static final String BASE_URL ="https://image.tmdb.org/t/p/w185";

    private TextView txtTitre;
    private ImageView imgFilm;
    private RatingBar ratingBar;
    private TextView txtVote;
    private TextView txtDescription;
    private LinearLayout layoutAddComment;
    private TextView txtConnecte;
    private EditText txtComment;
    private ImageButton btnSendComment;
    private RecyclerView recyclerView;
    private Dialog rankDialog;

    private String movieTitle;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        txtTitre = (TextView) findViewById(R.id.titre_film);
        imgFilm = (ImageView) findViewById(R.id.imageViewFilm);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        txtVote = (TextView) findViewById(R.id.vote_film);
        txtDescription = (TextView) findViewById(R.id.description_film);
        txtComment = (EditText) findViewById(R.id.txt_comment);
        btnSendComment = (ImageButton) findViewById(R.id.btn_submit_comment);
        layoutAddComment = (LinearLayout) findViewById(R.id.layout_add_comment);
        txtConnecte = (TextView) findViewById(R.id.txt_connecte);
        recyclerView = (RecyclerView) findViewById(R.id.fragment_main_recycler_view);

        Intent intent = getIntent();
        final Movie mov_intent = (Movie) intent.getSerializableExtra("detail");
        movieTitle = mov_intent.getTitle();

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
                        RatingHelper.createRateForMovie(ratingBar.getRating(), mov_intent.getTitle(), FirebaseGestion.modelCurrentUser);
                        Toast.makeText(getApplicationContext(), "Vous avez noté ce film "+ratingBar.getRating()+"/10", Toast.LENGTH_SHORT).show();
                        rankDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                rankDialog.show();
                return false;
            }
        });

        if (FirebaseGestion.modelCurrentUser==null)
        {
            layoutAddComment.setVisibility(View.GONE);
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
                    CommentHelper.createMessageForChat(commentContent, mov_intent.getTitle(), FirebaseGestion.modelCurrentUser);

                    // 3 - Reset text field
                    txtComment.setText("");
                }
            }
        });

        this.configureRecyclerView(movieTitle);
    }

    private void configureRecyclerView(String movieTitle)
    {
        //Track current chat name
        this.movieTitle = movieTitle;

        //Configure Adapter & RecyclerView

        String userId = "";
        if (FirebaseGestion.modelCurrentUser!=null) userId = FirebaseGestion.modelCurrentUser.getUid();
        this.commentAdapter = new CommentAdapter(generateOptionsForAdapter(CommentHelper.getAllCommentsForMovie(this.movieTitle)), this, userId);

        commentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                recyclerView.smoothScrollToPosition(commentAdapter.getItemCount()); // Scroll to bottom on new messages
            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(this.commentAdapter);

    }



    // 6 - Create options for RecyclerView from a Query

    private FirestoreRecyclerOptions<Comment> generateOptionsForAdapter(Query query)
    {
        return new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).setLifecycleOwner(this).build();
    }

    @Override
    public void onDataChanged()
    {

    }
}
