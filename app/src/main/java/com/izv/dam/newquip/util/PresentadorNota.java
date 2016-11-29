package com.izv.dam.newquip.util;

import android.content.Context;
import android.database.Cursor;

import com.izv.dam.newquip.util.contrato.ContratoNota;
import com.izv.dam.newquip.util.pojo.Lista;
import com.izv.dam.newquip.util.pojo.Nota;

public class PresentadorNota implements ContratoNota.InterfacePresentador {

    private ContratoNota.InterfaceModelo modelo;
    private ContratoNota.InterfaceVista vista;

    public PresentadorNota(ContratoNota.InterfaceVista vista) {
        this.vista = vista;
        this.modelo = new ModeloNota((Context)vista);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
    }

    @Override
    public long onSaveNota(Nota n) {
        return this.modelo.saveNota(n);
    }

    @Override
    public long onSaveLista(Lista l) {
        return this.modelo.saveLista(l);
    }

    public Cursor getListas(long id) {
        return this.modelo.getListas(id);
    }

    public int deleteLista(int id) {
        return this.modelo.deleteLista(id);
    }

}