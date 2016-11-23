package com.izv.dam.newquip.vistas.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;
import com.izv.dam.newquip.R;
import com.izv.dam.newquip.adaptadores.AdaptadorRecycle;
import com.izv.dam.newquip.contrato.ContratoMain;
import com.izv.dam.newquip.dialogo.DialogoBorrar;
import com.izv.dam.newquip.dialogo.OnBorrarDialogListener;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.vistas.notas.VistaNota;




public class VistaQuip extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ContratoMain.InterfaceVista , OnBorrarDialogListener {

    private AdaptadorRecycle adaptador;
    private PresentadorQuip presentador;
    private RecyclerView rec;
    private RecyclerView recList;
    private Toolbar toolbar;
    private Cursor cursor;
    private Cursor cursorList;
    private CardView card;
    private LinearLayout ly;
    private FloatingActionButton agregar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
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
        //recList = (RecyclerView) findViewById(R.id.recyclerLista);
        rec.setHasFixedSize(true);
        //recList.setHasFixedSize(true);

        adaptador = new AdaptadorRecycle(cursor,this);
        //adaptadorLista = new AdaptadorRecycleLista(cursorList, this);

        rec.setAdapter(adaptador);
        //recList.setAdapter(adaptadorLista);

        getContentResolver();
        rec.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));




/*
        ListView lv = (ListView) findViewById(R.id.lvListaNotas);
        adaptador = new AdaptadorNota(this, null);
        lv.setAdapter(adaptador);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                presentador.onEditNota(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(VistaQuip.this, "delete", Toast.LENGTH_SHORT).show();
                presentador.onShowBorrarNota(i);
                return true;
            }
        });
        */

        FloatingActionButton not = (FloatingActionButton) findViewById(R.id.action_b);
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentador.onAddNota();
            }
        });

    }

    private void init() {

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
        Toast.makeText(VistaQuip.this, "Nueva Nota", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, VistaNota.class);
        startActivity(i);
    }

    @Override
    public void mostrarDatos(Cursor c) {
        adaptador.changeCursor(c);
    }

    @Override
    public void mostrarEditarNota(Nota n) {
        Toast.makeText(VistaQuip.this, "Editar nota", Toast.LENGTH_SHORT).show();
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}