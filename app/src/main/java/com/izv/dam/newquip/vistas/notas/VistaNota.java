package com.izv.dam.newquip.vistas.notas;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.izv.dam.newquip.BuildConfig;
import com.izv.dam.newquip.R;
import com.izv.dam.newquip.contrato.ContratoNota;
import com.izv.dam.newquip.pojo.Lista;
import com.izv.dam.newquip.pojo.Nota;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VistaNota extends AppCompatActivity implements ContratoNota.InterfaceVista {


    private static final int MY_PERMISSIONS = 20;
    private CollapsingToolbarLayout barraTitulo;
    private EditText editTextTitulo, editTextNota;
    private Nota nota = new Nota();
    private PresentadorNota presentador;
    private Toolbar toolbar;
    private ImageView imagen;
    private BitmapFactory.Options options;
    private LinearLayout ly;
    private com.getbase.floatingactionbutton.FloatingActionButton agregar;
    private ImageButton button;
    private final static int CAMERA_REQUEST = 1;
    private final static int GALERY_REQUEST = 2;
    private int categoria;
    private String mCurrentPhotoPath;
    private Uri file;
    private String rutaTemp;
    private static final String NOMBRE_CARPETA_APP = "com.izv.dam.newquip";
    private static final String GENERADOS = "MisArchivos";
    private String ruta = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        presentador = new PresentadorNota(this);
        imagen = (ImageView) findViewById(R.id.imagen);
        nota.setIdEtiqueta(-1);

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

        init();

        mostrarNota(nota);
       /* if(ruta != null){
            setPic(ruta);
        }*/
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

        outState.putString("titulo", nota.getTitulo());
        outState.putString("contenido", nota.getContenido());
        outState.putParcelable("nota", nota);
        outState.putString("foto",mCurrentPhotoPath);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //recuperar
        String titulo = savedInstanceState.getString("titulo");
        editTextTitulo.setText(titulo);
        String contenido = savedInstanceState.getString("contenido");
        editTextNota.setText(contenido);
        ruta = savedInstanceState.getString("foto");
        //  setPic(savedInstanceState.getString("foto"));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (nota.getFoto() != null) {
            mCurrentPhotoPath = nota.getFoto();
            setPic(mCurrentPhotoPath);
        }
    }

    @Override
    public void mostrarNota(Nota n) {
        editTextTitulo.setText(nota.getTitulo());
        editTextNota.setText(nota.getContenido());
        mostrarListas(n.getId());
        Log.v("prueba","llegado");

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
        nota.setContenido(editTextNota.getText().toString());
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //AÑADIR CATEGORIA
        com.getbase.floatingactionbutton.FloatingActionButton addCategoria = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.addCategoria);
        addCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Trabajo", "Compras", "Ocio", "Otros"};

                AlertDialog.Builder builder = new AlertDialog.Builder(VistaNota.this);
                builder.setTitle(R.string.categorias);
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

        CollapsingToolbarLayout a = (CollapsingToolbarLayout) findViewById(R.id.barraTitulo);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                final CharSequence[] items = {"Sacar una foto", "Elegir de la galeria"};

                AlertDialog.Builder builder = new AlertDialog.Builder(VistaNota.this);
                builder.setTitle(R.string.menuCamara);
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
                        } else if (items[item].equals("Elegir de la galeria")) {
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, GALERY_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        });

        //AÑADIR LISTA
        ly = (LinearLayout) findViewById(R.id.listaEnNota);
        agregar = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.addLista);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.imprimir, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.op_imprimir:
                second sc = new second();
                sc.execute();
                return true;
            case R.id.op_camera:

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
                final CharSequence[] items = {"Sacar una foto", "Elegir de la galeria"};

                AlertDialog.Builder builder = new AlertDialog.Builder(VistaNota.this);
                builder.setTitle(R.string.menuCamara);
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
                        } else if (items[item].equals("Elegir de la galeria")) {
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, GALERY_REQUEST);
                        }
                    }
                });
                builder.show();
                return true;
            default:
                return true;
        }
    }


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
            if (resultCode == Activity.RESULT_CANCELED) {

            } else if (resultCode == Activity.RESULT_OK) {
                mCurrentPhotoPath = rutaTemp;
                galleryAddPic(mCurrentPhotoPath);
                setPic(mCurrentPhotoPath);
                nota.setFoto(mCurrentPhotoPath);
            }
        }

        if (requestCode == GALERY_REQUEST && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            if (getRealPath(selectedImage).length() > 1) {
                mCurrentPhotoPath = getRealPath(selectedImage);
                setPic(mCurrentPhotoPath);
                nota.setFoto(mCurrentPhotoPath);
            }
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
        if(scaleFactor == 1 ){
            scaleFactor = scaleFactor*2;
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        imagen.setImageBitmap(bitmap);
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

    class second extends AsyncTask<Void, Void, File> {


        @Override
        protected File doInBackground(Void... voids) {
            return generaPDF();
        }

        @Override
        protected void onPostExecute(File o) {
            super.onPostExecute(o);
            muestraPDF(o, getBaseContext());
        }
    }

    public File generaPDF() {
        Document document = new Document(PageSize.LETTER);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String NOMBRE_ARCHIVO = timeStamp + ".pdf";
        String tarjetaSD = Environment.getExternalStorageDirectory().toString();
        File pdfDir = new File(tarjetaSD + File.separator + NOMBRE_CARPETA_APP);

        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }

        File pdfSubDir = new File(pdfDir.getPath() + File.separator + GENERADOS);
        if (!pdfSubDir.exists()) {
            pdfSubDir.mkdir();
        }

        String nombre_completo = Environment.getExternalStorageDirectory() + File.separator + NOMBRE_CARPETA_APP + File.separator + GENERADOS + File.separator + NOMBRE_ARCHIVO;
        File outputfile = new File(nombre_completo);
        if (outputfile.exists()) {
            outputfile.delete();
        }

        //COMPROBAR SI EL ARCHIVO EXISTE EN LA CARPETA PARA NO SOBRESCRIBIRLO
        try {
            PdfWriter pdfwritter = PdfWriter.getInstance(document, new FileOutputStream(nombre_completo));
            //Crear el documento para escribirlo
            document.open();

            String htmlToPDF = "<html><><html>";
            editTextTitulo = (EditText) findViewById(R.id.etTitulo);
            String Titulo = editTextTitulo.getText().toString();

            editTextNota = (EditText) findViewById(R.id.etNota);
            String Contenido = editTextNota.getText().toString();

            document.add(new Paragraph("Titulo de la Nota"));
            document.add(new Paragraph(Titulo));

            document.add(new Paragraph(Contenido));

            Chunk chunk = new Chunk("");
            chunk.setBackground(BaseColor.GRAY);
            // Let's create de first Chapter (Creemos el primer capítulo)
            Chapter chapter = new Chapter(new Paragraph("Imagen"), 1);
            Image image;
            try {
                image = Image.getInstance(nota.getFoto());
                image.setAbsolutePosition(10, 10);
                image.setPaddingTop(20);

                float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() + 50) / image.getWidth()) * 100;

                image.scalePercent(scaler);
                chapter.add(image);

            } catch (BadElementException ex) {
                System.out.println("Image BadElementException" + ex);
            } catch (IOException ex) {
                System.out.println("Image IOException " + ex);
            }
            document.add(chapter);

            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
            try {
                worker.parseXHtml(pdfwritter, document, new StringReader(htmlToPDF));
                document.close();
                //Toast.makeText(VistaNota.this, "Se ha generado el PDF", Toast.LENGTH_LONG).show();
                return new File(nombre_completo);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (DocumentException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void muestraPDF(File archivo, Context context) {
        //Toast.makeText(context, "Leyendo el Archivo", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri file = FileProvider.getUriForFile(VistaNota.this,
                BuildConfig.APPLICATION_ID + ".provider",
                generaPDF());
        intent.setDataAndType(file, "aplication/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No tiene una app para abrir este tipo de archivos", Toast.LENGTH_LONG).show();
        }
    }

}

