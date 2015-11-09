package es.upm.miw.ficheros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class FicherosActivity extends AppCompatActivity {

    private final String PREF_KEY_NOMBRE_FICHERO = "nombre_fichero";

    private final String PREF_KEY_ALMACENAMIENTO_SD = "almacenamiento_sd";

    private String NOMBRE_FICHERO = "miFichero.txt";

    private boolean almacenarSD = false;

    private String RUTA_FICHERO;         /** SD card **/

    EditText lineaTexto;

    Button botonAniadir;

    TextView contenidoFichero;

    @Override
    protected void onResume() {
        super.onResume();
        updatePreferences();
        invalidateOptionsMenu();
    }

    private void updatePreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NOMBRE_FICHERO = prefs.getString(PREF_KEY_NOMBRE_FICHERO, NOMBRE_FICHERO);
        almacenarSD = prefs.getBoolean(PREF_KEY_ALMACENAMIENTO_SD, almacenarSD);
        if (almacenarSD) {
            RUTA_FICHERO = Environment.getExternalStorageDirectory() + "/" + NOMBRE_FICHERO;
            Log.i("Almacenando", "Almacenando en SD. Ruta: " + RUTA_FICHERO);
        } else {
            RUTA_FICHERO = getFilesDir() + "/" + NOMBRE_FICHERO;
            Log.i("Almacenando", "Almacenando en memoria interna. Ruta: " + RUTA_FICHERO);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mostrarContenido(contenidoFichero);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros);

        lineaTexto       = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir     = (Button)   findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);

    }

    /**
     * Al pulsar el botón añadir -> añadir al fichero.
     * Después de añadir -> mostrarContenido()
     *
     * @param v Botón añadir
     */
    public void accionAniadir(View v) {

        updatePreferences();

        /** Comprobar estado SD card **/
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {  // Añadir al fichero
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {  /** SD card **/
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
                fos.write(lineaTexto.getText().toString().getBytes());
                fos.write('\n');
                fos.close();
                mostrarContenido(contenidoFichero);
                Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Se pulsa sobre el textview -> mostrar contenido del fichero
     * Si está vacío -> mostrar un Toast
     *
     * @param textviewContenidoFichero TextView contenido del fichero
     */
    public void mostrarContenido(View textviewContenidoFichero) {

        updatePreferences();

        boolean hayContenido = false;
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();

        contenidoFichero.setText("");
        try {
            if (fichero.exists() &&         /** SD card **/
                    estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * Añade el menú con la opcion de vaciar el fichero
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflador del menú: añade elementos a la action bar
        getMenuInflater().inflate(R.menu.menu, menu);

        updateSettingsIcon(menu);

        return true;
    }

    private void updateSettingsIcon(Menu menu) {

        MenuItem item = menu.findItem(R.id.accionAjustes);
        if (almacenarSD) {
            item.setIcon(android.R.drawable.stat_notify_sdcard);
        } else {
            item.setIcon(android.R.drawable.stat_notify_sdcard_usb);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.accionVaciar:
                vaciarFichero();
                break;

            case R.id.accionAjustes:
                mostrarAjustes();
                break;

            case R.id.accionEliminarAlgunos:
                mostrarBorradorFicheros();
                break;

            case R.id.accionEliminarTodos:
                eliminarTodo();
                break;
        }

        return true;
    }

    private void vaciarFichero() {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {  // Vaciar el fichero
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) { /** SD card **/
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAjustes() {
        Intent intent = new Intent(getApplicationContext(), AjustesActivity.class);
        startActivity(intent);
    }

    private void mostrarBorradorFicheros() {
        Intent intent = new Intent(getApplicationContext(), BorradorFicheros.class);
        startActivity(intent);
    }

    /**
     * Eliminar todos los ficheros del directorio en uso
     */
    public void eliminarTodo() {
        GestorFicheros.eliminarDir(Environment.getExternalStorageDirectory() + "");
        GestorFicheros.eliminarDir(getFilesDir() + "");
        mostrarContenido(contenidoFichero);
    }

}
