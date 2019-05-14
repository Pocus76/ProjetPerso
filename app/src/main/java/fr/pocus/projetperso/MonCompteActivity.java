package fr.pocus.projetperso;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import fr.pocus.projetperso.comUtil.FirebaseGestion;
import fr.pocus.projetperso.objets.User;

/**
 * Created by Pocus on 14/05/2019.
 */

public class MonCompteActivity extends AppCompatActivity
{
    private TextView txtNom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_compte);

        txtNom = findViewById(R.id.txt_nom_user);

        User user = FirebaseGestion.modelCurrentUser;
        txtNom.setText(user.getUsername());
    }
}
