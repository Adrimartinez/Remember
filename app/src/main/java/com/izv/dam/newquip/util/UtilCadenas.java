package com.izv.dam.newquip.util;

import android.util.Log;

/**
 * Created by X on 31/10/2016.
 */

public class UtilCadenas {
    public static String getCondicionesSql(String condicionesIniciales, String nuevaCondicion){

        return getCondicionesSql(condicionesIniciales, nuevaCondicion, "and");
    }

    public static String getCondicionesSql(String condicionesIniciales, String nuevaCondicion, String conector){

        if( condicionesIniciales == null || condicionesIniciales.trim().length() == 0){
            return nuevaCondicion;
        }

        return condicionesIniciales + " " + conector + " " + nuevaCondicion;
    }

    public static String[] getNewArray(String[] arrayIni, String parametro){
        String [] newArray;
        if(arrayIni == null){
            Log.v("ES NULL", "arraIni");
            return new String[]{parametro};
        }

        newArray = new String[arrayIni.length + 1];

        for (int i = 0; i < arrayIni.length; i++) {
            newArray[i] = arrayIni[i];
        }

        arrayIni[newArray.length] = parametro;
        return newArray;
    }
}
