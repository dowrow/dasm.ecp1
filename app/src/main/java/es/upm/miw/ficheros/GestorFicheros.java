package es.upm.miw.ficheros;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class GestorFicheros {

    public static ArrayList<String> getFicheros(String dir) {
        ArrayList<String> filenames = new ArrayList<>();
        File f = new File(dir);
        for (File file :  f.listFiles()) {
            filenames.add(file + "");
        }
        return filenames;
    }

    public static void eliminarDir(String dir) {

        Log.i("Eliminando directorio", dir);

        try {
            File f = new File(dir);
            File files[] = f.listFiles();
            for (File file : files) {
                eliminarFichero(file + "");
            }
        } catch (Exception e) {
            Log.e("Error elim. directorio", dir + "");
        }
    }

    public static void eliminarFichero(String filename) {

        Log.i("Eliminando fichero", filename);

        try {
            File file = new File(filename);
            boolean deleted = file.delete();
        } catch (Exception e) {
            Log.e("Error elim. fichero", filename);
        }
    }

}
