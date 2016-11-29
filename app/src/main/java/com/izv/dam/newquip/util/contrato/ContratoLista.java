package com.izv.dam.newquip.util.contrato;

import com.izv.dam.newquip.util.pojo.Lista;

/**
 * Created by dam on 24/10/16.
 */

public interface ContratoLista {

    interface InterfaceModelo {

        void close();

        Lista getLista(long id);

        long saveLista(Lista n);

    }

    interface InterfacePresentador {

        void onPause();

        void onResume();

        long onSaveLista(Lista n);

    }

    interface InterfaceVista {

        void mostrarLista(Lista n);

    }

}
