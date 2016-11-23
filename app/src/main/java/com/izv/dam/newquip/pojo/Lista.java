package com.izv.dam.newquip.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.izv.dam.newquip.contrato.ContratoBaseDatos;

/**
 * Created by dam on 21/10/16.
 */

public class Lista implements Parcelable {
    private long id;
    private long idNota;
    private String contenido;
    private int marca;

    public Lista() {
        this(0, null, 0, 0);
    }

    public Lista(int marca, String contenido, long idNota, long id) {
        this.marca = marca;
        this.contenido = contenido;
        this.idNota = idNota;
        this.id = id;
    }

    public int isMarca() {
        return marca;
    }

    public void setMarca(int marca) {
        this.marca = marca;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId(String id) {
        try {
            this.id = Long.parseLong(id);
        } catch (NumberFormatException e) {
            this.id = 0;
        }
    }

    public long getIdNota() {
        return idNota;
    }

    public void setIdNota(long idNota) {
        this.idNota = idNota;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public ContentValues getContentValues() {
        return this.getContentValues(false);
    }

    public ContentValues getContentValues(boolean withId) {
        ContentValues valores = new ContentValues();
        if (withId) {
            valores.put(ContratoBaseDatos.TablaLista._ID, this.getId());
        }
        valores.put(ContratoBaseDatos.TablaLista.CONTENIDO, this.getContenido());
        valores.put(ContratoBaseDatos.TablaLista.ID_NOTA, this.getIdNota());
        valores.put(ContratoBaseDatos.TablaLista.MARCA, this.isMarca());
        return valores;
    }

    public static Lista getLista(Cursor c) {
        Lista objeto = new Lista();
        objeto.setId(c.getLong(c.getColumnIndex(ContratoBaseDatos.TablaLista._ID)));
        objeto.setContenido(c.getString(c.getColumnIndex(ContratoBaseDatos.TablaLista.CONTENIDO)));
        objeto.setIdNota(c.getLong(c.getColumnIndex((ContratoBaseDatos.TablaLista.ID_NOTA))));
        objeto.setMarca(c.getInt(c.getColumnIndex(ContratoBaseDatos.TablaLista.MARCA)));
        //String hecho = c.getString(c.getColumnIndex(ContratoBaseDatos.TablaLista.HECHO)); objeto.setHecho(Boolean.valueOf(hecho));
        return objeto;
    }

    @Override
    public String toString() {
        return "Lista{" +
                "id=" + id +
                ", contenido='" + contenido + '\'' +
                ", id_nota='" + idNota + '\'' +
                ", marca='" + marca + '\'' +
                '}';
    }

    protected Lista(Parcel in) {
    }

    public static final Creator<Lista> CREATOR = new Creator<Lista>() {
        @Override
        public Lista createFromParcel(Parcel in) {
            return new Lista(in);
        }

        @Override
        public Lista[] newArray(int size) {
            return new Lista[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(contenido);
        dest.writeLong(idNota);
        dest.writeInt(marca);
    }
}
