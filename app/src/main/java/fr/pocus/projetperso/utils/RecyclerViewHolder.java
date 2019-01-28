package fr.pocus.projetperso.utils;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.pocus.projetperso.R;
import fr.pocus.projetperso.objets.Comment;

/**
 * Created by Pocus on 25/01/2019.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.nom_item) TextView txtNom;
    @BindView(R.id.date_item) TextView txtDate;
    @BindView(R.id.description_item) TextView txtDescription;

    public RecyclerViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithComment(Comment comment, String currentUserId)
    {
        if (comment.getUserSender()!=null)
        {
            this.txtNom.setText(comment.getUserSender().getUsername());
            this.txtDate.setText(DateUtils.toDateTimeString(comment.getDateCreated()));
            this.txtDescription.setText(comment.getMessage());

            if (!currentUserId.equals(""))
            {
                Boolean isCurrentUser = comment.getUserSender().getUid().equals(currentUserId);
                if (isCurrentUser)
                {
                    SpannableString content = new SpannableString(txtNom.getText().toString());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    txtNom.setText(content);
                }
            }
        }

    }
}
