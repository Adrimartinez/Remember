package com.izv.dam.newquip.vistas.notas;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.izv.dam.newquip.BuildConfig;
import com.izv.dam.newquip.R;
import com.izv.dam.newquip.contrato.ContratoNota;
import com.izv.dam.newquip.pojo.Lista;
import com.izv.dam.newquip.pojo.Nota;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VistaNota extends AppCompatActivity implements ContratoNota.InterfaceVista {


    private static final int MY_PERMISSIONS = 20;
    private static final int PERMISSION_CAMERA = 21;
    private static final int PERMISSION_STORAGE = 22;
    private CollapsingToolbarLayout barraTitulo;
    private EditText editTextTitulo, editTextNota;
    private Nota nota = new Nota();
    private PresentadorNota presentador;
    private Toolbar toolbar;
    private ImageView imagen;
    private BitmapFactory.Options options;
    private LinearLayout ly;
    private com.getbase.floatingactionbutton.FloatingActionButton agregar;
    //private EditText oculto;
    //private LayoutInflater li;
    private ImageButton button;
    private final static int CAMERA_REQUEST = 1;
    private final static int GALERY_REQUEST = 2;
    private com.getbase.floatingactionbutton.FloatingActionButton camara;
    private int categoria;
    private String mCurrentPhotoPath;
    private Uri file;
    private String rutaTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        presentador = new PresentadorNota(this);
        imagen = (ImageView) findViewById(R.id.imagen);

        editTextTitulo = (EditText) findViewById(R.id.etTitulo);
        editTextNota = (EditText) findViewById(R.id.etNota);

        if (savedInstanceState != null) {
            nota = savedInstanceState.getParcelable("nota");
        } else {
            Bundle b = getIntent().getExtras();
            if (b != null) {
                nota = b.getParcelable("nota");
            }
        }
        mostrarNota(nota);
        init();

    }

    @Override
    protected void onPause() {
        saveNota();
        saveListas();
        presentador.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        presentador.onResume();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("nota", nota);
    }

    @Override
    public void mostrarNota(Nota n) {
        editTextTitulo.setText(nota.getTitulo());
        editTextNota.setText(nota.getNota());
        mostrarListas(n.getId());
    }

    public void mostrarListas(long id) {
        Cursor cursorListas = presentador.getListas(id);
        ly = (LinearLayout) findViewById(R.id.listaEnNota);
        while (cursorListas.moveToNext()) {
            String contenido = cursorListas.getString(2);
            int marca = cursorListas.getInt(3);
            final LayoutInflater li = LayoutInflater.from(VistaNota.this);
            final RelativeLayout card = (RelativeLayout) li.inflate(R.layout.activity_content_lista, null, false);
            EditText contenidoL = (EditText) card.findViewById(R.id.contLista);
            contenidoL.setText(contenido);
            CheckBox checkBox = (CheckBox) card.findViewById(R.id.checkBox);
            final EditText oculto = (EditText) card.findViewById(R.id.oculto);
            oculto.setText(cursorListas.getString(0));
            if (marca == 0) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
            button = (ImageButton) card.findViewById(R.id.delete);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = Integer.parseInt(oculto.getText().toString());
                    if (id != 0) {
                        Log.v("BORRANDO", "ID!!!!=0");
                        presentador.deleteLista(id);
                        ((LinearLayout) card.getParent()).removeView(card);
                    } else {
                        Log.v("BORRANDO", "ID=0");
                        ((LinearLayout) card.getParent()).removeView(card);
                    }
                }
            });

            ly.addView(card);
        }
    }

    private void saveNota() {
        nota.setTitulo(editTextTitulo.getText().toString());
        nota.setNota(editTextNota.getText().toString());
        nota.setFoto(mCurrentPhotoPath);
        long r = presentador.onSaveNota(nota);
        if (r > 0 & nota.getId() == 0) {
            nota.setId(r);
        }
    }

    private void saveListas() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.listaEnNota);
        for (int i = 2; i < layout.getChildCount(); i++) {
            Lista lista = new Lista();
            View v = layout.getChildAt(i);
            //contenido
            EditText texto = (EditText) v.findViewById(R.id.contLista);
            String contenido = texto.getText().toString();
            if (contenido.equals("") || contenido == null) {
                //Si no tiene contenido la lista no se guarda
            } else {
                lista.setContenido(contenido);
                //checkbox/marca
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                if (checkBox.isChecked()) {
                    lista.setMarca(1);
                } else {
                    lista.setMarca(0);
                }
                //id
                lista.setIdNota(nota.getId());

                //MODIFICAR (si no tiene id será 0 y se creará nueva)
                EditText oculto = (EditText) v.findViewById(R.id.oculto);
                lista.setId(oculto.getText().toString());
                presentador.onSaveLista(lista);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void init() {

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

//        BitmapFactory.decodeResource(getResources(), R.drawable.androidcpu, options);
//
//        BitmapDrawable im = new BitmapDrawable(getResources(), imagenResuelta(getResources(), R.drawable.androidcpu, 250, 200));
//
//        imagen = (ImageView) findViewById(R.id.imagen);
//        imagen.setBackground(im);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.inflateMenu(R.drawable.ic_insert_drive_file_black_24dp);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //AÑADIR FOTO
        com.getbase.floatingactionbutton.FloatingActionButton camara = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.addFoto);
        //camara = (FloatingActionButton) findViewById(R.id.camara);
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(VistaNota.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(VistaNota.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(VistaNota.this, Manifest.permission.CAMERA)
                            && ActivityCompat.shouldShowRequestPermissionRationale(VistaNota.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {

                        ActivityCompat.requestPermissions(VistaNota.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS);
                    }
                }

                final CharSequence[] items = {"Sacar una foto", "Elegir de galeria"};

                AlertDialog.Builder builder = new AlertDialog.Builder(VistaNota.this);
                builder.setTitle("Elige imagen");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Sacar una foto")) {
                            try {
                                takePicture();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else if (items[item].equals("Elegir de galeria")) {
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, GALERY_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        });

        //AÑADIR CATEGORIA
        com.getbase.floatingactionbutton.FloatingActionButton addCategoria = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.addCategoria);
        addCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Trabajo", "Compras", "Ocio", "Otros"};

                AlertDialog.Builder builder = new AlertDialog.Builder(VistaNota.this);
                builder.setTitle("Elige una categoria");
                if (nota.getIdEtiqueta() > -1) {
                    categoria = (int) nota.getIdEtiqueta();
                } else {
                    categoria = -1;
                }
                builder.setSingleChoiceItems(items, categoria, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Trabajo")) {
                            nota.setIdEtiqueta(0);
                        } else if (items[item].equals("Compras")) {
                            nota.setIdEtiqueta(1);
                        } else if (items[item].equals("Ocio")) {
                            nota.setIdEtiqueta(2);
                        } else if (items[item].equals("Otros")) {
                            nota.setIdEtiqueta(3);
                        }
                    }
                });
                builder.show();
            }
        });

        //AÑADIR LISTA
        ly = (LinearLayout) findViewById(R.id.listaEnNota);
        agregar = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.addLista);
        CollapsingToolbarLayout a = (CollapsingToolbarLayout) findViewById(R.id.barraTitulo);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater li = LayoutInflater.from(VistaNota.this);
                final RelativeLayout card = (RelativeLayout) li.inflate(R.layout.activity_content_lista, null, false);
                final EditText oculto = (EditText) card.findViewById(R.id.oculto);
                oculto.setText("0");
                button = (ImageButton) card.findViewById(R.id.delete);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = Integer.parseInt(oculto.getText().toString());
                        if (id != 0) {
                            Log.v("BORRANDO", "ID!!!!=0");
                            presentador.deleteLista(id);
                            ((LinearLayout) card.getParent()).removeView(card);
                        } else {
                            Log.v("BORRANDO", "ID=0");
                            ((LinearLayout) card.getParent()).removeView(card);
                        }
                    }
                });
                ly.addView(card);
            }
        });
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LayoutInflater li = LayoutInflater.from(VistaNota.this);
                final RelativeLayout card = (RelativeLayout) li.inflate(R.layout.activity_content_lista, null, false);
                final EditText oculto = (EditText) card.findViewById(R.id.oculto);
                oculto.setText("0");
                button = (ImageButton) card.findViewById(R.id.delete);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = Integer.parseInt(oculto.getText().toString());
                        if (id != 0) {
                            Log.v("BORRANDO", "ID!!!!=0");
                            presentador.deleteLista(id);
                            ((LinearLayout) card.getParent()).removeView(card);
                        } else {
                            Log.v("BORRANDO", "ID=0");
                            ((LinearLayout) card.getParent()).removeView(card);
                        }
                    }
                });
                ly.addView(card);
            }
        });

        barraTitulo = (CollapsingToolbarLayout) findViewById(R.id.barraTitulo);
        barraTitulo.setTitle(editTextTitulo.getEditableText());

    }

    /*

    public static int calcularTamaño(BitmapFactory.Options options, int reqAlto, int reqAncho) {

        int tamaño = 1;

        int ancho = options.outWidth;
        int alto = options.outHeight;
        String tipo = options.outMimeType;

        if (alto > reqAlto || ancho > reqAncho) {
            final int nuevoAlto = alto / 2;
            final int nuevoAncho = ancho / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((nuevoAlto / tamaño) >= reqAlto
                    && (nuevoAncho / tamaño) >= reqAncho) {
                tamaño *= 2;
            }
        }

        return tamaño;

    }

    public static Bitmap imagenResuelta(Resources res, int resId, int reqAlto, int reqAncho) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calcularTamaño(options, reqAncho, reqAlto);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }*/

    private String getRealPath(Uri datos) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(datos, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            if(resultCode == Activity.RESULT_CANCELED){

            }else if (resultCode == Activity.RESULT_OK){
                mCurrentPhotoPath = rutaTemp;
                Log.v("ruta",mCurrentPhotoPath);
                galleryAddPic(mCurrentPhotoPath);
                setPic(mCurrentPhotoPath);
                Log.v("ruta buena",mCurrentPhotoPath);
                nota.setFoto(mCurrentPhotoPath);
            }

//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//
//            BitmapDrawable im = new BitmapDrawable(photo);
//
//            imagen.setBackground(im);
//            imagen.setImageBitmap(photo);
        }
        if (requestCode == GALERY_REQUEST && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            if(getRealPath(selectedImage).length() > 1){
                mCurrentPhotoPath = getRealPath(selectedImage);
                setPic(mCurrentPhotoPath);
                nota.setFoto(mCurrentPhotoPath);
            }

            //imagen.setImageURI(selectedImage);
        }
    }

    private File createImageFile() throws IOException {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "NewQuipPictures");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File f = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        rutaTemp = f.getAbsolutePath();
        return f;
    }

    public void takePicture() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = FileProvider.getUriForFile(VistaNota.this,
                BuildConfig.APPLICATION_ID + ".provider",
                createImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        intent.putExtra("data", file);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void galleryAddPic(String pathFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(pathFile));
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic(String path) {
        // Get the dimensions of the View
        int targetW = imagen.getWidth();
        int targetH = imagen.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        imagen.setImageBitmap(bitmap);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (nota.getFoto() != null) {
            mCurrentPhotoPath = nota.getFoto();
            setPic(mCurrentPhotoPath);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
            }
        }
    }
}

