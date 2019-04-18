package fr.pocus.projetperso.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.pocus.projetperso.R;
import fr.pocus.projetperso.objets.Rating;

/**
 * Created by Pocus on 20/03/2019.
 */

public class RatingAdapter extends BaseAdapter
{
    private final ArrayList mData;

    public RatingAdapter(HashMap<String, Rating> map)
    {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Map.Entry<String, Rating> getItem(int position)
    {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final View result;

        if (convertView == null)
        {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_ratings, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, Rating> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(android.R.id.text1)).setText(item.getKey());
        ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue().getRating()+"/10");

        return result;
    }
}
