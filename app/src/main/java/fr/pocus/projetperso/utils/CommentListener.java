package fr.pocus.projetperso.utils;

import java.util.List;

import fr.pocus.projetperso.objets.Comment;

/**
 * Created by Pocus on 18/04/2019.
 */

public interface CommentListener
{
    void commentGet(List<Comment> comments);
}
