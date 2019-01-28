package fr.pocus.projetperso.utils;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import fr.pocus.projetperso.R;
import fr.pocus.projetperso.objets.Comment;

/**
 * Created by Pocus on 25/01/2019.
 */

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, RecyclerViewHolder>
{
    public interface Listener
    {
        void onDataChanged();
    }

    private final String idCurrentUser;
    private Listener callback;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Listener callback, String idCurrentUser)
    {
        super(options);
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }


    @Override

    protected void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position, @NonNull Comment model)
    {
        holder.updateWithComment(model, this.idCurrentUser);
    }

    @Override

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }


    @Override

    public void onDataChanged()
    {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}
