package es.upm.miw.ficheros;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BorradorFicheros extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrador_ficheros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_borrador_ficheros);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Coger todos los ficheros en SD y alm. interno
        final ArrayList<String> ficheros = GestorFicheros.getFicheros(getFilesDir() + "");
        ficheros.addAll(GestorFicheros.getFicheros(Environment.getExternalStorageDirectory() + ""));

        // Crear adapter y mostrar elementos
        ListView lvFicheros = (ListView)findViewById(R.id.lvFicheros);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ficheros);
        lvFicheros.setAdapter(adapter);
        lvFicheros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(BorradorFicheros.this);
                adb.setTitle("Borrar Fichero");
                adb.setMessage("¿Estás seguro de que quieres borrar el siguiente fichero?\n" + ficheros.get(position));
                final int positionToRemove = position;
                adb.setNegativeButton("Cancelar", null);
                adb.setPositiveButton("Borrar", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GestorFicheros.eliminarFichero(ficheros.get(positionToRemove));
                        ficheros.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.txtFicheroBorrado), Toast.LENGTH_SHORT).show();
                    }
                });
                adb.show();
            }
        });
    }

}
