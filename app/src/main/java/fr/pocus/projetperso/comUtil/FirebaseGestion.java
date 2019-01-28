package fr.pocus.projetperso.comUtil;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import fr.pocus.projetperso.objets.User;

/**
 * Created by Pocus on 24/01/2019.
 */

public class FirebaseGestion
{
    private static final String TAG = "FIREBASE_GESTION";

    public static User modelCurrentUser = null;


    @Nullable
    public static FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    public static Boolean isCurrentUserLogged(){ return (getCurrentUser() != null); }

    public static void getCurrentUserFromFirestore()
    {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }
}
