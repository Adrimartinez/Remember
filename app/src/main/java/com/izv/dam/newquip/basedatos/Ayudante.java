package com.izv.dam.newquip.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.izv.dam.newquip.contrato.ContratoBaseDatos;

public class Ayudante extends SQLiteOpenHelper {

    //sqlite
    //tipos de datos https://www.sqlite.org/datatype3.html
    //fechas https://www.sqlite.org/lang_datefunc.html
    //trigger https://www.sqlite.org/lang_createtrigger.html

    public static final int VERSION = 4;

    public Ayudante(Context context) {
        super(context, ContratoBaseDatos.BASEDATOS, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;
        sql = "create table if not exists " + ContratoBaseDatos.TablaNota.TABLA +
                " (" +
                ContratoBaseDatos.TablaNota._ID + " integer primary key autoincrement , " +
                ContratoBaseDatos.TablaNota.TITULO + " text, " +
                ContratoBaseDatos.TablaNota.NOTA + " text, " +
                ContratoBaseDatos.TablaNota.FOTO + " text, " +
                ContratoBaseDatos.TablaNota.ID_ETIQUETAS + " integer " +
                " ); ";
        db.execSQL(sql);
        sql =   "create table if not exists " + ContratoBaseDatos.TablaLista.TABLA +
                " (" +
                ContratoBaseDatos.TablaLista._ID + " integer primary key autoincrement , " +
                ContratoBaseDatos.TablaLista.ID_NOTA + " integer, " +
                ContratoBaseDatos.TablaLista.CONTENIDO + " text, " +
                ContratoBaseDatos.TablaLista.MARCA + " integer " +
                " ); ";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + ContratoBaseDatos.TablaNota.TABLA + "; ";
        db.execSQL(sql);
        sql = "drop table if exists " + ContratoBaseDatos.TablaLista.TABLA + "; ";
        db.execSQL(sql);
    }
}