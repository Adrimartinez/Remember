package com.izv.dam.newquip.proveedor;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.gestion.GestionLista;
import com.izv.dam.newquip.gestion.GestionNota;
import com.izv.dam.newquip.util.UtilCadenas;

import static com.izv.dam.newquip.contrato.ContratoBaseDatos.TablaNota.CONTENT_ITEM_TYPE;
import static com.izv.dam.newquip.contrato.ContratoBaseDatos.TablaNota.CONTENT_TYPE;

/**
 * Created by dam on 07/11/2016.
 */

public class ProviderQuip extends android.content.ContentProvider {
    //UriMatcher
    private static final UriMatcher uriMatcher;
    private static final int TODO_NOTA = 0;
    private static final int CONCRETO_NOTA = 1;
    private static final int TODO_LISTA = 2;
    private static final int CONCRETO_LISTA = 3;
    private GestionNota gestorNota;
    private GestionLista gestorLista;

    //Inicializamos el UriMatcher
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ContratoBaseDatos.AUTORIDAD, ContratoBaseDatos.TablaNota.TABLA, TODO_NOTA);
        uriMatcher.addURI(ContratoBaseDatos.AUTORIDAD, ContratoBaseDatos.TablaNota.TABLA + "/#", CONCRETO_NOTA);
        uriMatcher.addURI(ContratoBaseDatos.AUTORIDAD, ContratoBaseDatos.TablaLista.TABLA, TODO_LISTA);
        uriMatcher.addURI(ContratoBaseDatos.AUTORIDAD, ContratoBaseDatos.TablaLista.TABLA + "/#", CONCRETO_LISTA);
    }

    public ProviderQuip() {
        //gestor = new GestionNota(getContext());
    }

    @Override
    public boolean onCreate() {
        gestorNota = new GestionNota(getContext());
        gestorLista = new GestionLista(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        int tipo = uriMatcher.match(uri);
        if (tipo < 0) {
            throw new IllegalArgumentException("Nope");
        }
        switch (tipo) {
            case CONCRETO_NOTA:
                String id = uri.getLastPathSegment();
                String newSelection = UtilCadenas.getCondicionesSql(selection, ContratoBaseDatos.TablaNota._ID + " = ? ");
                String[] newSelectionArgs = UtilCadenas.getNewArray(selectionArgs, id);
                cursor = gestorNota.getCursor(projection, newSelection, newSelectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TODO_NOTA:
                cursor = gestorNota.getCursor(projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CONCRETO_LISTA:
                String idLista = uri.getLastPathSegment();
                String newSelectionLista = UtilCadenas.getCondicionesSql(selection, ContratoBaseDatos.TablaLista._ID + " = ? ");
                String[] newSelectionArgsLista = UtilCadenas.getNewArray(selectionArgs, idLista);
                cursor = gestorLista.getCursor(projection, newSelectionLista, newSelectionArgsLista, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TODO_LISTA:
                cursor = gestorLista.getCursor(projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TODO_NOTA:
                return CONTENT_TYPE;
            case CONCRETO_NOTA:
                return CONTENT_ITEM_TYPE;
            case TODO_LISTA:
                return CONTENT_TYPE;
            case CONCRETO_LISTA:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int tipo = uriMatcher.match(uri);
        Log.v("insert","1");
        Log.v("URI:   ",uri.toString());
        long id = 0;
        if (tipo == TODO_NOTA) {
            Log.v("insertNota","2");
            id = gestorNota.insert(values);
        }else if(tipo == TODO_LISTA){
            id = gestorLista.insert(values);
            Log.v("insertLista","1");
        }
        if (id > 0) {
            Log.v("insert","3");
            Uri uriGetter = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(uri, null);
            return uriGetter;
        }
        throw new IllegalArgumentException("Provider Insert Error");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int borrados = 0;
        int tipo = uriMatcher.match(uri);

        switch (tipo) {
            case CONCRETO_NOTA:
                String id = uri.getLastPathSegment();
                String newSelection = UtilCadenas.getCondicionesSql(selection, ContratoBaseDatos.TablaNota._ID + " = ? ");
                String[] newSelectionArgs = UtilCadenas.getNewArray(selectionArgs, id);
                int listasBorradas = gestorLista.delete("ID_NOTA='"+id+"'", null);
                borrados = gestorNota.delete(newSelection, newSelectionArgs);
                Log.v("LISTAS BORRADAS:", Integer.toString(listasBorradas));
                Log.v("deleteNotaCONCRETO", uri.toString());
                break;
            case TODO_NOTA:
                borrados = gestorNota.delete(selection, selectionArgs);
                Log.v("deleteNotaTODO", uri.toString());
                break;
            case CONCRETO_LISTA:
                String idLista = uri.getLastPathSegment();
                String newSelectionLista = UtilCadenas.getCondicionesSql(selection, ContratoBaseDatos.TablaNota._ID + " = ? ");
                String[] newSelectionArgsLista = UtilCadenas.getNewArray(selectionArgs, idLista);
                borrados = gestorLista.delete(newSelectionLista, newSelectionArgsLista);
                Log.v("deleteListaCONCRETO", uri.toString());
                break;
            case TODO_LISTA:
                borrados = gestorLista.delete(selection, selectionArgs);
                Log.v("deleteListaTODO", uri.toString());
                break;
            default:
                throw new IllegalArgumentException("Provider Delete Error");
        }
        if (borrados > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return borrados;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int tipo = uriMatcher.match(uri);
        int actualizados = 0;
        if (tipo < 0) {
            throw new IllegalArgumentException("no siguas fallando");
        }
        switch (tipo) {
            case CONCRETO_NOTA:
                String id = uri.getLastPathSegment();
                String newSelection = UtilCadenas.getCondicionesSql(selection, ContratoBaseDatos.TablaNota._ID + " = ? ");
                String[] newSelectionArgs = UtilCadenas.getNewArray(selectionArgs, id);
                actualizados = gestorNota.update(values, newSelection, newSelectionArgs);
                Log.v("UpdateNotaCONCRETO", "b"+newSelection);
                break;
            case TODO_NOTA:
                actualizados = gestorNota.update(values, selection, selectionArgs);
                Log.v("UpdateNotaTODO", "b"+selection);
                break;
            case CONCRETO_LISTA:
                String idLista = uri.getLastPathSegment();
                String newSelectionLista = UtilCadenas.getCondicionesSql(selection, ContratoBaseDatos.TablaNota._ID + " = ? ");
                String[] newSelectionArgsLista = UtilCadenas.getNewArray(selectionArgs, idLista);
                actualizados = gestorLista.update(values, newSelectionLista, newSelectionArgsLista);
                Log.v("UpdateNotaCONCRETO", "b"+newSelectionLista);
                break;
            case TODO_LISTA:
                actualizados = gestorLista.update(values, selection, selectionArgs);
                Log.v("UpdateListaTODO", "b"+selection);
                break;
            default:
                throw new IllegalArgumentException("Provider Delete Error");
        }

        if (actualizados > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return actualizados;

    }
}