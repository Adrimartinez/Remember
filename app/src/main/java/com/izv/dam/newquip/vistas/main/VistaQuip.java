package com.izv.dam.newquip.vistas.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.adaptadores.AdaptadorRecycle;
import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.contrato.ContratoMain;
import com.izv.dam.newquip.dialogo.DialogoBorrar;
import com.izv.dam.newquip.dialogo.OnBorrarDialogListener;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.vistas.notas.VistaNota;


public class VistaQuip extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ContratoMain.InterfaceVista, OnBorrarDialogListener, LoaderManager.LoaderCallbacks<Cursor> {

    private AdaptadorRecycle adaptador;
    private PresentadorQuip presentador;
    private RecyclerView rec;
    private Cursor cursor;
/*
    class sincrona extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mostrarDatos(cursor);

        }
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //sincrona sc = new sincrona();
        //sc.execute();

    }

    private void init() {

        presentador = new PresentadorQuip(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        rec = (RecyclerView) findViewById(R.id.recycler);
        rec.setHasFixedSize(true);
        adaptador = new AdaptadorRecycle(cursor, this);
        rec.setAdapter(adaptador);

        rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FloatingActionButton not = (FloatingActionButton) findViewById(R.id.action_b);
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentador.onAddNota();
            }
        });
    }


    @Override
    protected void onPause() {
        presentador.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        presentador.onResume();
        super.onResume();
    }


    @Override
    public void mostrarAgregarNota() {
        Intent i = new Intent(this, VistaNota.class);
        startActivity(i);
    }

    @Override
    public void mostrarDatos(Cursor c) {
        adaptador.changeCursor(c);
    }

    @Override
    public void mostrarEditarNota(Nota n) {
        Intent i = new Intent(this, VistaNota.class);
        Bundle b = new Bundle();
        b.putParcelable("nota", n);
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public void mostrarConfirmarBorrarNota(Nota n) {
        DialogoBorrar fragmentBorrar = DialogoBorrar.newInstance(n);
        fragmentBorrar.show(getSupportFragmentManager(), "Dialogo borrar");

    }

    @Override
    public void onBorrarPossitiveButtonClick(Nota n) {
        presentador.onDeleteNota(n);
    }

    @Override
    public void onBorrarNegativeButtonClick() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        TextView textViewCategoria = (TextView)findViewById(R.id.textViewCategoria);
        if (id == R.id.nav_trabajo) {
            textViewCategoria.setText("Filtrado por: Trabajo");
            presentador.filterCategorias(0);
        } else if (id == R.id.nav_compras) {
            textViewCategoria.setText("Filtrado por: Compras");
            presentador.filterCategorias(1);
        } else if (id == R.id.nav_ocio) {
            textViewCategoria.setText("Filtrado por: Ocio");
            presentador.filterCategorias(2);
        } else if (id == R.id.nav_otros) {
            textViewCategoria.setText("Filtrado por: Otros");
            presentador.filterCategorias(3);
        }else if (id == R.id.nav_todo) {
            textViewCategoria.setText("Mostrando Todo");
            presentador.onResume();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //LOADER
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContratoBaseDatos.TablaNota.URI_NOTA, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adaptador != null) {
            adaptador.swapCursor(data);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaptador.swapCursor(null);
    }
}