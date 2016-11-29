package com.izv.dam.newquip.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.izv.dam.newquip.util.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.util.contrato.ContratoNota;
import com.izv.dam.newquip.util.pojo.Lista;
import com.izv.dam.newquip.util.pojo.Nota;

public class ModeloNota implements ContratoNota.InterfaceModelo {


    private Uri uriNota = ContratoBaseDatos.TablaNota.URI_NOTA;
    private Uri uriLista = ContratoBaseDatos.TablaLista.URI_LISTA;
    private Context c;
    private ContentResolver resolver;
    private Cursor cursor;

    //private GestionNota gn = null;

    public ModeloNota(Context c) {
        this.c = c;
        this.resolver = c.getContentResolver();
    }

    @Override
    public void close() {
        //gn.close();
    }

    @Override
    public Nota getNota() {
        cursor = this.resolver.query(uriNota,null,null,null,null);
        Nota n = Nota.getNota(cursor);
        return n;
    }

    @Override
    public long saveNota(Nota n) {
        if(n.getId()==0) {
            Uri uri = this.insertNota(n);
            if(uri != null){
                return ContentUris.parseId(uri);
            }
            return 0;
        } else {
            return this.updateNota(n);
        }
    }

    private long deleteNota(Nota n) {
        Uri u = Uri.withAppendedPath(uriNota, n.getId()+"");
        this.resolver.delete(u, null, null);
        Log.v("deleteNotaModeloNotaN", uriNota.toString());
        return 1;
    }

    private Uri insertNota(Nota n) {
        if(n.getNota().trim().compareTo("")==0 && n.getTitulo().trim().compareTo("")==0) {
            return null;
        }
        //return gn.insert(n);
        Log.v("insertNotaN", uriNota.toString());
        Log.v("trim", Integer.toString(n.getNota().trim().compareTo("")));
        return this.resolver.insert(uriNota, n.getContentValues());
    }

    private int updateNota(Nota n) {
        if(n.getNota().trim().compareTo("")==0 && n.getTitulo().trim().compareTo("")==0) {
            this.deleteNota(n);
            Log.v("updateNota0", uriNota.toString());
            return 0;
        }
        Log.v("updateNotaN", uriNota.toString());
        Uri u = Uri.withAppendedPath(uriNota, n.getId()+"");
        return this.resolver.update(u, n.getContentValues(), null, null);
    }


    //LISTAS

    /*public Lista getLista(long idLista) {
        String idS = Long.toString(idLista);
        String[] args = new String[] {idS};
        cursor = this.resolver.query(uriLista,null,"id=?", args,null);
        Lista l = Lista.getLista(cursor);
        return l;
    }*/
    public Cursor getListas(long idNota) {
        String idS = Long.toString(idNota);
        //String[] args = new String[] {idS};
        Cursor cursorListas = this.resolver.query(uriLista,null,"ID_NOTA='"+idS+"'", null,null);
        return cursorListas;
    }

    public long saveLista(Lista l) {
        if(l.getId()==0) {
            Uri uri = this.insertLista(l);
            return ContentUris.parseId(uri);
        } else {
            Log.v("IR A UPDATE: ", "--");
            return this.updateLista(l);
        }
    }

    @Override
    public int deleteLista(int l) {
        Uri u = Uri.withAppendedPath(uriLista, Integer.toString(l)+"");
        this.resolver.delete(u, null, null);
        Log.v("deleteListaModeloN", uriLista.toString());
        return 1;
    }

    private Uri insertLista(Lista l) {
        if(l.getContenido().trim().compareTo("")==0) {
            return null;
        }
        Log.v("insertListaN", uriLista.toString());
        return this.resolver.insert(uriLista, l.getContentValues());
    }

    private int updateLista(Lista l) {
        if(l.getContenido().trim().compareTo("")==0) {
            this.deleteLista((int) l.getId());
            Log.v("updateLista0", uriLista.toString());
            return 0;
        }
        Log.v("updateListaN", uriLista.toString());
        Uri u = Uri.withAppendedPath(uriLista, l.getId()+"");
        return this.resolver.update(u, l.getContentValues(), null, null);
    }
}