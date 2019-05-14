package fr.pocus.projetperso.utils;

import java.util.HashMap;

import fr.pocus.projetperso.objets.Rating;

/**
 * Created by Pocus on 18/04/2019.
 */

public interface FirebaseListener
{
    void ratingGet(HashMap<String, Rating> listeNotes);
    void favorisGet(HashMap<String, Boolean> listeFavoris);
}
