package fr.pocus.projetperso;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.pocus.projetperso.comUtil.FirebaseGestion;
import fr.pocus.projetperso.comUtil.NetworkUtils;
import fr.pocus.projetperso.comUtil.UserHelper;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.utils.AlarmReceiver;
import fr.pocus.projetperso.utils.DateUtils;
import fr.pocus.projetperso.utils.MovieAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    @BindView(R.id.indeterminateBar) ProgressBar mProgressBar;
    @BindView(R.id.pop_movies_grid) GridView mGridView;
    @BindView(R.id.txtFilter) TextView txtFilter;
    @BindView(R.id.layout_main) ConstraintLayout coordinatorLayout;
    private Menu actionsMenu;
    private MenuItem btnConnexion;
    private MenuItem btnMonCompte;
    private MenuItem btnFilmsNotes;
    private NavigationView navigationView;
    private TextView txtNomUser;
    private TextView txtMailUser;
    public static ArrayList<Movie> mPopularList;
    public static ArrayList<Movie> mTopTopRatedList;
    public static ArrayList<Movie> mTheaterList;
    private static final int RC_SIGN_IN = 123;
    private static final int SIGN_OUT_TASK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);
        Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE); //Hide Progressbar by Default
        new CommunicationApi().execute(); //New code

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menuSide = navigationView.getMenu();
        btnMonCompte = menuSide.findItem(R.id.btn_mon_compte);
        btnFilmsNotes = menuSide.findItem(R.id.btn_films_notes);
        btnFilmsNotes.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Intent intentFilmsNotes = new Intent(MainActivity.this, FilmsNotesActivity.class);
                startActivity(intentFilmsNotes);
                return false;
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> view, View view1, int position, long l)
            {
                Movie movie = (Movie) view.getAdapter().getItem(position);
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra("detail", movie);
                startActivity(intent);
            }
        });
    }

    private void createUserInFirestore()
    {
        if (FirebaseGestion.getCurrentUser() != null)
        {
            String username = FirebaseGestion.getCurrentUser().getDisplayName();
            String uid = FirebaseGestion.getCurrentUser().getUid();
            UserHelper.createUser(uid, username).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    FirebaseGestion.getCurrentUserFromFirestore();
                }
            });
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        actionsMenu = menu;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        txtNomUser = (TextView) headerView.findViewById(R.id.nom_utilisateur);
        txtMailUser = (TextView) headerView.findViewById(R.id.mail_utilisateur);
        txtNomUser.setText("");
        txtMailUser.setText("Vous n'êtes pas connecté");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.pop_movies)
        {
            refreshList(mPopularList);
            txtFilter.setText("Les plus populaires");
        }
        if (id == R.id.top_movies)
        {
            refreshList(mTopTopRatedList);
            txtFilter.setText("Les mieux notés");
        }
        if (id == R.id.theater_movies)
        {
            refreshList(mTheaterList);
            txtFilter.setText("Actuellement au cinéma");
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList(ArrayList<Movie> list)
    {
        MovieAdapter adapter = new MovieAdapter(MainActivity.this, list);
        mGridView.invalidateViews();
        mGridView.setAdapter(adapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btn_connexion)
        {
            btnConnexion = item;
            startSignInActivity();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startSignInActivity()
    {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_auth)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void showSnackBar(ConstraintLayout coordinatorLayout, String message)
    {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // 3 - Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data)
    {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN)
        {
            if (resultCode == RESULT_OK)
            {
                this.createUserInFirestore();
                showSnackBar(this.coordinatorLayout, "Connexion réussie");
                txtNomUser.setText(FirebaseGestion.getCurrentUser().getDisplayName());
                txtMailUser.setText(FirebaseGestion.getCurrentUser().getEmail());
                btnMonCompte.setVisible(true);
                btnFilmsNotes.setVisible(true);
                btnConnexion.setTitle("Se déconnecter");
                btnConnexion.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_logout));

                btnConnexion.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        signOutUserFromFirebase();
                        return true;
                    }
                });
            }
            else
            { // ERRORS
                if (response == null)
                {
                    showSnackBar(this.coordinatorLayout, "Connexion perdue");
                }
                else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
                {
                    showSnackBar(this.coordinatorLayout, "Vous n'êtes pas connecté à internet");
                }
                else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                {
                    showSnackBar(this.coordinatorLayout, "Une erreur s'est produite");
                }

            }
        }
    }
    private void signOutUserFromFirebase()
    {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        FirebaseGestion.modelCurrentUser = null;
    }

    // 3 - Create OnCompleteListener called after tasks ended

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin)
    {
        return new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                if (origin==SIGN_OUT_TASK)
                {
                    txtNomUser.setText("");
                    txtMailUser.setText("Vous n'êtes pas connecté");
                    btnFilmsNotes.setVisible(false);
                    btnMonCompte.setVisible(false);
                    btnConnexion.setTitle("Se connecter");
                    btnConnexion.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_auth));
                    btnConnexion.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            startSignInActivity();
                            return true;
                        }
                    });
                }
            }
        };
    }

    private class CommunicationApi extends AsyncTask<String, Void, String>
    {
        private static final String TAG = "COM_API";
        private static final String API_URL = "http://api.themoviedb.org/3/discover/";
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
            String popularMoviesURL = API_URL+"movie?sort_by=popularity.desc&api_key="+API_KEY+"&language=fr";
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            cal.add(Calendar.MONTH, -1);
            Date aMonthAgo = cal.getTime();
            String sNow = DateUtils.toJsonString(now);
            String sMonthAgo = DateUtils.toJsonString(aMonthAgo);
            String theaterMoviesURL = API_URL+"movie?primary_release_date.gte="+sMonthAgo+"&primary_release_date.lte="+sNow+"&api_key="+API_KEY+"&language=fr";
            String topRatedMoviesURL = API_URL+"movie?sort_by=vote_average.desc&api_key="+API_KEY+"&language=fr";

            mPopularList = new ArrayList<>();
            mTopTopRatedList = new ArrayList<>();
            mTheaterList = new ArrayList<>();

            try
            {
                if(NetworkUtils.networkStatus(MainActivity.this))
                {
                    mPopularList = NetworkUtils.fetchData(popularMoviesURL); //Get popular movies
                    mTopTopRatedList = NetworkUtils.fetchData(topRatedMoviesURL); //Get top rated movies
                    mTheaterList = NetworkUtils.fetchData(theaterMoviesURL); //Get top rated movies
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Vous n'êtes pas connecté à internet",Toast.LENGTH_LONG).show();
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
                    refreshList(mPopularList);
                }
            });
            txtFilter.setText("Les plus populaires");
        }
    }

}
