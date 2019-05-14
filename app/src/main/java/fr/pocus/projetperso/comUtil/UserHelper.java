package fr.pocus.projetperso.comUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.pocus.projetperso.MainActivity;
import fr.pocus.projetperso.objets.User;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * Created by Pocus on 25/01/2019.
 */

public class UserHelper
{
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username)
    {
        // 1 - Create User object
        User userToCreate = new User(uid, username);

        // 2 - Add a new User Document to Firestore
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid)
    {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid)
    {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsMentor(String uid, Boolean isMentor)
    {
        return UserHelper.getUsersCollection().document(uid).update("isMentor", isMentor);
    }

    // --- DELETE ---
    public static Task<Void> deleteUser(final String uid, final Context context)
    {
        Log.d("-----------", "deleteUser: "+uid);
        return UserHelper.getUsersCollection().document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Intent intent = new Intent(context, MainActivity.class);
                int mPendingIntentId = 1;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });
    }
}
