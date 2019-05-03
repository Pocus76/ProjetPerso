package fr.pocus.projetperso.utils;

import android.content.Context;
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
    Context context;
    String[] data;
    private static LayoutInflater inflater = null;

    public RatingAdapter(Context context, String[] data)
    {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return data.length;
    }

    @Override
    public Object getItem(int position)
    {
        return data[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item, null);
        TextView text = vi.findViewById(R.id.text);
        text.setText(data[position]);
        return vi;
    }
}
