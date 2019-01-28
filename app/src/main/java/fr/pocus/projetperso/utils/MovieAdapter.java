package fr.pocus.projetperso.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.pocus.projetperso.R;
import fr.pocus.projetperso.objets.Movie;
import fr.pocus.projetperso.objets.ViewHolder;

/**
 * Created by Pocus on 24/01/2019.
 */

public class MovieAdapter extends BaseAdapter
{
    public static final String MOVIE_BASE_URL="https://image.tmdb.org/t/p/w185";
    private Context mContext;
    ArrayList<Movie> list;

    public MovieAdapter(Context context, ArrayList<Movie> movieList)
    {
        this.mContext = context;
        this.list = movieList;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Movie getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        Movie movies = getItem(position);
        if (convertView == null)
        {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.imageView.setAdjustViewBounds(true);
            // store the holder with the view.
            convertView.setTag(viewHolder);
        }
        else
        {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(movies.getTitle());
        viewHolder.textView.setTag(position);

        //load data into the ImageView using Picasso
        Picasso.get().load(MOVIE_BASE_URL + movies.getPosterPath())
                .placeholder(R.drawable.image_placeholder)
                .into(viewHolder.imageView);


        return convertView;
    }
}
