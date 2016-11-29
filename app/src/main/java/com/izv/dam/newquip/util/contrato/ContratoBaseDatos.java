package com.izv.dam.newquip.util.contrato;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ContratoBaseDatos {


    public final static String BASEDATOS = "quiip.sqlite";

    public final static String AUTORIDAD = "com.izv.dam.newquip.proveedor";
    public final static Uri CONTENT_URI = Uri.parse("content://"+AUTORIDAD);

    private ContratoBaseDatos() {
    }

    public static abstract class TablaNota implements BaseColumns {
        //BaseColumns incluye de forma predeterminada el campo _id
        public static final String TABLA = "nota";
        public static final String TITULO = "titulo";
        public static final String NOTA = "nota";
        public static final String FOTO = "foto";
        public static final String ID_ETIQUETAS = "id_etiquetas";
        public static final String[] PROJECTION_ALL = {_ID, TITULO, NOTA, ID_ETIQUETAS};
        public static final String SORT_ORDER_DEFAULT = _ID + " desc";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTORIDAD + " . " + TABLA;
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTORIDAD + " . " + TABLA;
        public final static Uri URI_NOTA = Uri.withAppendedPath(CONTENT_URI, ContratoBaseDatos.TablaNota.TABLA);
    }
    public static abstract class TablaLista implements BaseColumns{
        public static final String TABLA = "lista";
        public static final String ID_NOTA = "ID_NOTA";
        public static final String CONTENIDO = "contenido";
        public static final String MARCA = "marca";
        public static final String[] PROJECTION_ALL = {_ID, CONTENIDO, ID_NOTA, MARCA};
        public static final String SORT_ORDER_DEFAULT = _ID + " desc";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTORIDAD + " . " + TABLA;
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTORIDAD + " . " + TABLA;
        public final static Uri URI_LISTA = Uri.withAppendedPath(CONTENT_URI, ContratoBaseDatos.TablaLista.TABLA);
    }
    public abstract static class Join{
        public static final String TABLA = "joinnotalista";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTORIDAD + " . " + TABLA;
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTORIDAD + " . " + TABLA;
    }
}
