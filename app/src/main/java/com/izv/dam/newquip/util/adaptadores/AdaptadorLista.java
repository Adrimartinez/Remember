package com.izv.dam.newquip.util.adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.util.pojo.Lista;

public class AdaptadorLista extends CursorAdapter{



    public AdaptadorLista(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_content_lista, parent, false);
        
        return itemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvN = (TextView) view.findViewById(R.id.contLista);
        Lista n = Lista.getLista(cursor);
        tvN.setText(n.getContenido());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
