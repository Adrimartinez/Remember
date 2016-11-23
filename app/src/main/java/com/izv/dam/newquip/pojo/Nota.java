package com.izv.dam.newquip.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.izv.dam.newquip.contrato.ContratoBaseDatos;

import java.sql.Blob;

public class Nota implements Parcelable {

    private long id, idEtiqueta;
    private String titulo, nota, foto;



    public Nota() {
        this(0, null, null, null, 0);
    }

    public Nota(long id, String titulo, String foto, String nota, int idEtiqueta) {
        this.id = id;
        this.titulo = titulo;
        this.nota = nota;
        this.foto = foto;
        this.idEtiqueta = idEtiqueta;
    }

    protected Nota(Parcel in) {
        id = in.readLong();
        titulo = in.readString();
        nota = in.readString();
        foto = in.readString();
        idEtiqueta = in.readLong();
    }

    public static final Creator<Nota> CREATOR = new Creator<Nota>() {
        @Override
        public Nota createFromParcel(Parcel in) {
            return new Nota(in);
        }

        @Override
        public Nota[] newArray(int size) {
            return new Nota[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId(String id) {
        try {
            this.id = Long.parseLong(id);
        } catch(NumberFormatException e){
            this.id = 0;
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public long getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(long idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public ContentValues getContentValues(){
        return this.getContentValues(false);
    }

    public ContentValues getContentValues(boolean withId){
        ContentValues valores = new ContentValues();
        if(withId){
            valores.put(ContratoBaseDatos.TablaNota._ID, this.getId());
        }
        valores.put(ContratoBaseDatos.TablaNota.TITULO, this.getTitulo());
        valores.put(ContratoBaseDatos.TablaNota.NOTA, this.getNota());
        valores.put(ContratoBaseDatos.TablaNota.FOTO, this.getFoto());
        valores.put(ContratoBaseDatos.TablaNota.ID_ETIQUETAS, this.getIdEtiqueta());
        return valores;
    }

    public static Nota getNota(Cursor c){
        Nota objeto = new Nota();
        objeto.setId(c.getLong(c.getColumnIndex(ContratoBaseDatos.TablaNota._ID)));
        objeto.setTitulo(c.getString(c.getColumnIndex(ContratoBaseDatos.TablaNota.TITULO)));
        objeto.setNota(c.getString(c.getColumnIndex(ContratoBaseDatos.TablaNota.NOTA)));
        objeto.setFoto(c.getString(c.getColumnIndex((ContratoBaseDatos.TablaNota.FOTO))));
        objeto.setIdEtiqueta(c.getLong(c.getColumnIndex(ContratoBaseDatos.TablaNota.ID_ETIQUETAS)));
        return objeto;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", nota='" + nota + '\'' +
                ", foto='" + foto + '\'' +
                ", id_etiqueta='" + idEtiqueta + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(titulo);
        dest.writeString(nota);
        dest.writeString(foto);
        dest.writeLong(idEtiqueta);
    }
}