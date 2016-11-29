package com.izv.dam.newquip.util.adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.util.VistaNota;
import com.izv.dam.newquip.util.dialogo.DialogoBorrar;
import com.izv.dam.newquip.util.main.VistaQuip;
import com.izv.dam.newquip.util.pojo.Lista;
import com.izv.dam.newquip.util.pojo.Nota;

/**
 * Created by dam on 25/10/16.
 */

public class AdaptadorRecycle extends RecyclerView.Adapter<AdaptadorRecycle.NotasViewHolder> {

    private Cursor cursor;
    private boolean mDataValid;
    private int mRowIDColumn;
    private Context context;
    final VistaQuip vista = new VistaQuip();


    public static class NotasViewHolder extends RecyclerView.ViewHolder {

        private TextView txTitulo;
        private TextView txContenido;
        private ImageView imagen;


        public NotasViewHolder(View itemView) {
            super(itemView);

            txTitulo = (TextView) itemView.findViewById(R.id.cardTitulo);
            txContenido = (TextView) itemView.findViewById(R.id.cardContenido);
           // imagen = (ImageView) itemView.findViewById(R.id.imagenRecycler);
        }



        public void bindNota(Nota n) {
            txTitulo.setText(n.getTitulo());
            txContenido.setText(n.getNota());
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = false;
            op.inSampleSize = 100;

            Bitmap bit = BitmapFactory.decodeFile(n.getFoto(),op);
            //imagen.setImageBitmap(bit);

        }

    }

    public static class ListasViewHolder extends RecyclerView.ViewHolder{

        private TextView txTitulo;
        private TextView txContenido;

        public ListasViewHolder(View itemView) {
            super(itemView);

            txContenido = (TextView) itemView.findViewById(R.id.contLista);
        }

        public void bindLista(Lista l){
            txContenido.setText(l.getContenido());
        }
    }

    public AdaptadorRecycle(Cursor cursor, Context context) {
        this.context = context;
        this.cursor = cursor;
        setHasStableIds(true);
        swapCursor(cursor);
    }


    @Override
    public NotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_nota, viewGroup, false);
        final RecyclerView rec = (RecyclerView) viewGroup.findViewById(R.id.recycler);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final CharSequence[] items = {"Editar","Borrar"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Opciones");
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(items[item].equals("Editar")){
                            Cursor c = getCursor();
                            int position = rec.getChildAdapterPosition(v);
                            c.moveToPosition(position);
                            Nota n = Nota.getNota(c);
                            Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context, VistaNota.class);
                            Bundle b = new Bundle();
                            b.putParcelable("nota", n);
                            i.putExtras(b);
                            context.startActivity(i);
                        }else if (items[item].equals("Borrar")){
                            Cursor c = getCursor();
                            int position = rec.getChildAdapterPosition(v);
                            c.moveToPosition(position);
                            Nota n = Nota.getNota(c);
                            Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                            DialogoBorrar fragmentBorrar = DialogoBorrar.newInstance(n);
                            fragmentBorrar.show(((FragmentActivity) context).getSupportFragmentManager(), "Dialogo borrar");
                        }
                    }
                });
                builder.show();
            }
        });
       /* itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

            }
        });*/

        NotasViewHolder not = new NotasViewHolder(itemView);

        return not;
    }

    @Override
    public void onBindViewHolder(NotasViewHolder holder, int position) {

        cursor.moveToPosition(position);

        onBindViewHolder(holder, cursor);

    }


    public void onBindViewHolder(NotasViewHolder holder, Cursor position) {

        Nota item = Nota.getNota(position);

        holder.bindNota(item);

    }
/*
    public Cursor onChangeCursor (Cursor c){

        if(this.cursor != null){
            return null;
        }else if(cursor == this.cursor){

            return cursor;
        }
        return c;
    }
    */

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
        Cursor oldCursor = cursor;
        if (oldCursor != null) {
            if (mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
        }
        cursor = newCursor;
        if (newCursor != null) {
            if (mDataSetObserver != null) {
                newCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    protected Context getContext() {
        return context;
    }

    protected Cursor getCursor() {
        return cursor;
    }


    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetChanged();
        }
    };


    @Override
    public int getItemCount() {
        if (mDataValid && cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

}
