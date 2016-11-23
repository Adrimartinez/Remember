package com.izv.dam.newquip.gestion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.pojo.Lista;

/**
 * Created by dam on 24/10/16.
 */

public class GestionLista extends Gestion<Lista> {

    public GestionLista(Context c) {
        super(c);
    }

    public GestionLista(Context c, boolean write) {
        super(c, write);
    }

    public Lista get(Long id) {
        String where = ContratoBaseDatos.TablaLista._ID + " = ? ";
        String[] parametros = {id + ""};
        Cursor c = this.getCursor(ContratoBaseDatos.TablaLista.PROJECTION_ALL, where, parametros, null, null, ContratoBaseDatos.TablaLista.SORT_ORDER_DEFAULT);
        if (c.getCount() > 0) {
            c.moveToFirst();
            Lista lista = Lista.getLista(c);
            return lista;
        }
        return null;
    }

    @Override
    public long insert(Lista objeto) {
        return this.insert(ContratoBaseDatos.TablaLista.TABLA, objeto.getContentValues());
    }

    @Override
    public long insert(ContentValues objeto) {
        return this.insert(ContratoBaseDatos.TablaLista.TABLA, objeto);
    }

    @Override
    public int deleteAll() {
        return this.deleteAll(ContratoBaseDatos.TablaLista.TABLA);
    }

    @Override
    public int delete(Lista objeto) {
        String condicion = ContratoBaseDatos.TablaLista._ID + " = ?";
        String[] argumentos = {objeto.getId() + ""};
        return this.delete(ContratoBaseDatos.TablaLista.TABLA, condicion, argumentos);
    }

    @Override
    public int update(Lista objeto) {
        ContentValues valores = objeto.getContentValues();
        String condicion = ContratoBaseDatos.TablaLista._ID + " = ?";
        String[] argumentos = {objeto.getId() + ""};
        return this.update(ContratoBaseDatos.TablaLista.TABLA, valores, condicion, argumentos);
    }

    @Override
    public int update(ContentValues valores, String condicion, String[] argumentos) {
        return this.update(ContratoBaseDatos.TablaLista.TABLA, valores, condicion, argumentos);
    }

    @Override
    public Cursor getCursor() {
        return this.getCursor(ContratoBaseDatos.TablaLista.TABLA, ContratoBaseDatos.TablaLista.PROJECTION_ALL, ContratoBaseDatos.TablaLista.SORT_ORDER_DEFAULT);
    }

    @Override
    public Cursor getCursor(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return this.getCursor(ContratoBaseDatos.TablaLista.TABLA, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
    public int delete(String condicion, String[] argumentos) {
        return this.delete(ContratoBaseDatos.TablaLista.TABLA, condicion, argumentos);
    }
}
