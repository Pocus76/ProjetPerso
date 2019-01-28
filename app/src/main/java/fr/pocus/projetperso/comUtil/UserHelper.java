package fr.pocus.projetperso.comUtil;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.pocus.projetperso.objets.User;

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
    public static Task<Void> deleteUser(String uid)
    {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
