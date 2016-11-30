package com.izv.dam.newquip.vistas.main;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.contrato.ContratoMain;
import com.izv.dam.newquip.pojo.Nota;

public class ModeloQuip implements ContratoMain.InterfaceModelo {

    private Uri uri = ContratoBaseDatos.TablaNota.URI_NOTA;
    private Context c;
    private ContentResolver resolver;
    private Cursor cursor;

    public ModeloQuip(Context c) {
        this.c = c;
        this.resolver = c.getContentResolver();
    }

    @Override
    public void close() {
        //gn.close();
    }

    @Override
    public long deleteNota(Nota n) {
        String condicion = ContratoBaseDatos.TablaNota._ID + " = ?";
        String[] argumentos = { n.getId() + "" };
        Uri u = Uri.withAppendedPath(uri, n.getId()+"");
        Log.v("DeleteNotaModeloQuipN", uri.toString());
        return this.resolver.delete(u, null, null);
    }

    @Override
    public long deleteNota(int position) {
        this.cursor.moveToPosition(position);
        Nota n = Nota.getNota(cursor);
        Log.v("DeleteNotaModeloQuipP", uri.toString());
        return this.deleteNota(n);
    }

    @Override
    public Nota getNota(int position) {
        this.cursor.moveToPosition(position);
        Nota n = Nota.getNota(cursor);
        Log.v("getNotaModeloQuipP", uri.toString());
        return n;
    }

    @Override
    public void loadData(OnDataLoadListener listener) {
        cursor = this.resolver.query(uri,null,null,null,null);
        Log.v("loadDataModeloQuipP", uri.toString());
        listener.setCursor(cursor);
    }

    public void loadData(OnDataLoadListener listener, int categoria) {
        String cateS = Integer.toString(categoria);
        cursor = this.resolver.query(uri,null,"ID_ETIQUETAS='"+cateS+"'",null,null);
        Log.v("loadDataModeloQuipFilte", uri.toString());
        listener.setCursor(cursor);
    }
}