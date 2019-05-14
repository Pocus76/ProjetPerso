package fr.pocus.projetperso;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.regex.Pattern;

import fr.pocus.projetperso.comUtil.FirebaseGestion;
import fr.pocus.projetperso.comUtil.UserHelper;
import fr.pocus.projetperso.objets.User;

/**
 * Created by Pocus on 14/05/2019.
 */

public class MonCompteActivity extends AppCompatActivity
{
    private static final int SIGN_OUT_TASK = 10;

    private TextView txtNom;
    private Button btnContacteAdmin;
    private Button btnSupprCompte;
    private Button btnDeconnexion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_compte);

        txtNom = findViewById(R.id.txt_nom_user);
        btnContacteAdmin = findViewById(R.id.btn_contacte_administrateur);
        btnSupprCompte = findViewById(R.id.btn_suppr_compte);
        btnDeconnexion = findViewById(R.id.btn_deconnexion);

        final User user = FirebaseGestion.modelCurrentUser;
        txtNom.setText(user.getUsername());

        btnContacteAdmin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MonCompteActivity.this);

                LinearLayout layout = new LinearLayout(MonCompteActivity.this);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(parms);

                layout.setGravity(Gravity.CLIP_VERTICAL);
                layout.setPadding(2, 2, 2, 2);

                TextView tv2 = new TextView(MonCompteActivity.this);
                LinearLayout.LayoutParams tv2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tv2Params.bottomMargin = 5;
                tv2.setText("Veuillez entrer votre message");

                final EditText et2 = new EditText(MonCompteActivity.this);
                LinearLayout.LayoutParams et2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layout.addView(tv2,tv2Params);
                layout.addView(et2, et2Params);

                alertDialogBuilder.setView(layout);
                alertDialogBuilder.setTitle("Contacter un administrateur");
                alertDialogBuilder.setCancelable(true);

                // Setting Negative "Cancel" Button
                alertDialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                // Setting Positive "OK" Button
                alertDialogBuilder.setPositiveButton("Envoyer", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (et2.getText().toString().trim().equals(""))
                        {
                            et2.setError("Veuillez écrire votre message");
                            return;
                        }
                        Uri uri = Uri.parse("mailto:sonji@mlogicali.com");
                        Intent myActivity2 = new Intent(Intent.ACTION_SENDTO, uri);
                        myActivity2.putExtra(Intent.EXTRA_SUBJECT, "Message à l'attention des administrateurs");
                        myActivity2.putExtra(Intent.EXTRA_TEXT, et2.getText().toString().trim());
                        startActivity(myActivity2);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                try {
                    alertDialog.show();
                } catch (Exception e) {
                    // WindowManager$BadTokenException will be caught and the app would
                    // not display the 'Force Close' message
                    e.printStackTrace();
                }
            }
        });

        btnSupprCompte.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UserHelper.deleteUser(user.getUid(), getApplicationContext());
            }
        });

        btnDeconnexion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signOutUserFromFirebase();
            }
        });
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                int mPendingIntentId = 1;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        };
    }
}
