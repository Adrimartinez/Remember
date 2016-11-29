package com.izv.dam.newquip.util.contrato;

import android.database.Cursor;

import com.izv.dam.newquip.util.pojo.Lista;
import com.izv.dam.newquip.util.pojo.Nota;

public interface ContratoNota {

    interface InterfaceModelo {

        void close();

        Nota getNota();

        long saveNota(Nota n);

        long saveLista(Lista l);

        int deleteLista(int l);

        Cursor getListas(long id);

    }

    interface InterfacePresentador {

        void onPause();

        void onResume();

        long onSaveNota(Nota n);

        long onSaveLista(Lista l);

    }

    interface InterfaceVista {

        void mostrarNota(Nota n);

    }

}