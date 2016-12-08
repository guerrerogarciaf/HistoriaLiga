package fgaplicaciones.com.historialiga;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LigaDBHelper extends SQLiteOpenHelper {
    //The Android's default system path of your application database.
    private static String DB_PATH = "";
    private static String DB_NAME = "Liga";
    private static String DB_FICH = "Liga.sqlite";
    private SQLiteDatabase db;
    private final Context contexto;
    private static final String TAG = "HistoriaLiga";
    private boolean actualizar;
    /*
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     */
    public LigaDBHelper(Context context) {

        super(context, DB_NAME, null, 9999);
        this.contexto = context;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
    }
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void crear(Boolean actu) throws IOException {

        //     boolean dbExiste = existe();

        //      if(dbExiste){
        //do nothing - database already exist
        //      }else{
        //By calling this method and empty database will be created into the default system path
        //of your application so we are gonna be able to overwrite that database with our database.
        this.actualizar = actu;
        this.getReadableDatabase();
        try {
            copiar();
        } catch (IOException e) {
            throw new Error("Error copying database");
        }
    }
    // }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean existe() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = db.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.d(TAG, "Error en check");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copiar() throws IOException {
        InputStream myInput = null;
        //Open your local db as the input stream
        if (actualizar) {
     /*
            myInput = contexto.openFileInput(contexto.getFilesDir().getPath() +
                       "/Liga.sqlite");
     */
            myInput = contexto.openFileInput("Liga.sqlite");
        }
        else {
            myInput = contexto.getAssets().open(DB_FICH);
        }
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            Log.d(TAG, "Copiando");
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void abrir() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (db != null)
            db.close();

        super.close();

    }

    public Cursor obtenerGoleadores() {
        Cursor c = db.rawQuery("SELECT ROWID AS _id, TEMPORADA, JUGADOR, PAIS, COUNTRY, EQUIPO, " +
                "GOLES, PARTIDOS, PROMEDIO " +
                "FROM goleadores ORDER BY TEMPORADA DESC ", null);
        return c;
    }

    public Cursor obtenerCopa() {
        Cursor c = db.rawQuery("SELECT ROWID AS _id, TEMPORADA, CAMPEON, SUBCAMPEON, " +
                "(GOLES_C || ' - ' || GOLES_S) AS RESULTADO " +
                "FROM copa ORDER BY TEMPORADA DESC" , null);
        return c;
    }

    public Cursor obtenerGanadores(int div) {
        //Filtro: Temporada, Grupo
        Cursor c = null;
        switch (div) {
            case 0:
                c = db.rawQuery("SELECT ROWID AS _id, TEMPORADA, TEMPORADA AS TEXTO, EQUIPO " +
                        "FROM clasificaciones WHERE POSICION = 1 " +
         //              "AND TEMPORADA <> (SELECT MAX(TEMPORADA) FROM clasificaciones) " +
                        "ORDER BY TEMPORADA DESC", null);
                break;
            case 1:
                c = db.rawQuery("SELECT cl.ROWID AS _id, (cl.TEMPORADA || ' Gr. ' || cl.GRUPO) AS TEXTO, " +
                        "cl.EQUIPO AS EQUIPO, cl.GRUPO, cl.TEMPORADA " +
                        "FROM clasificaciones2 cl, temporadas2 tm WHERE POSICION = 1 " +
                        "AND cl.TEMPORADA = tm.TEMPORADA " +
                        "AND cl.GRUPO = tm.GRUPO " +
                        "AND tm.GRUPO <> '' " +
                        "UNION " +
                        "SELECT cl.ROWID AS _id, cl.TEMPORADA AS TEXTO, cl.EQUIPO AS EQUIPO, " +
                        "cl.GRUPO, cl.TEMPORADA " +
                        "FROM clasificaciones2 cl, temporadas2 tm WHERE POSICION = 1 " +
                        "AND cl.TEMPORADA <> (SELECT MAX(cl2.TEMPORADA) FROM clasificaciones2 cl2) " +
                        "AND cl.TEMPORADA = tm.TEMPORADA " +
                        "AND tm.GRUPO = '' " +
                        "ORDER BY cl.TEMPORADA DESC, cl.GRUPO ", null);
                break;
        }
        return c;
    }

    public Cursor obtenerClasificacion(int div, String[] filtro) {
        //Filtro: Temporada, Grupo
        Cursor c = null;
        switch (div) {
            case 0:
                c = db.rawQuery("SELECT ROWID AS _id, POSICION, EQUIPO, PT, PJ, PG, PE, PP, GF, GC," +
                        "PT_C, PJ_C, PG_C, PE_C, PP_C, GF_C, GC_C, PT_F, PJ_F, PG_F, PE_F, PP_F," +
                        "GF_F, GC_F FROM clasificaciones WHERE TEMPORADA = ?", filtro);
                break;
            case 1:
                c = db.rawQuery("SELECT ROWID AS _id, POSICION, EQUIPO, PT, PJ, PG, PE, PP, GF, GC," +
                        "PT_C, PJ_C, PG_C, PE_C, PP_C, GF_C, GC_C, PT_F, PJ_F, PG_F, PE_F, PP_F," +
                        "GF_F, GC_F FROM clasificaciones2 WHERE TEMPORADA = ? AND GRUPO = ?", filtro);
                break;
        }
        return c;
    }


    public Cursor obtenerTemporadas(int filtroDiv) {

        Cursor c = null;
        switch (filtroDiv) {
            case 0:
                c = db.rawQuery("SELECT ROWID AS _id, TEMPORADA AS TEXTO FROM clasificaciones " +
                        "GROUP BY TEMPORADA ORDER BY TEMPORADA DESC", null);
                break;
            case 1:
                c = db.rawQuery("SELECT ROWID AS _id, TEMPORADA, GRUPO, TEMPORADA AS TEXTO" +
                        " FROM temporadas2 " +
                        " WHERE GRUPO = '' AND RESULTADOS = 'S' " +
                        " UNION " +
                        "SELECT ROWID AS _id, TEMPORADA, GRUPO," +
                        "(TEMPORADA || ' Gr. ' || GRUPO) AS TEXTO FROM temporadas2" +
                        " WHERE GRUPO <> '' AND RESULTADOS = 'S' " +
                        " ORDER BY TEMPORADA DESC, GRUPO ASC", null);
                break;
        }
        return c;
    }

    public Cursor obtenerTemporadasRe(int filtroDiv) {

        Cursor c = null;
        switch (filtroDiv) {
            case 0:
                c = db.rawQuery("SELECT ROWID AS _id, TEMPORADA AS TEXTO FROM resultados " +
                        "GROUP BY TEMPORADA ORDER BY TEMPORADA DESC", null);
                break;
            case 1:
                c = db.rawQuery("SELECT ROWID AS _id, TEMPORADA, GRUPO, TEMPORADA AS TEXTO" +
                        " FROM temporadas2 " +
                        " WHERE GRUPO = '' AND CLASIFICACION = 'S' " +
                        " UNION " +
                        "SELECT ROWID AS _id, TEMPORADA, GRUPO," +
                        "(TEMPORADA || ' Gr. ' || GRUPO) AS TEXTO FROM temporadas2" +
                        " WHERE GRUPO <> '' AND CLASIFICACION = 'S' " +
                        " ORDER BY TEMPORADA DESC, GRUPO ASC", null);
                break;
        }
        return c;
    }

    public Cursor obtenerJornadas(int filtroDiv, String[] filtro) {

        Cursor c = null;
        switch (filtroDiv) {
            case 0:
                c = db.rawQuery("SELECT ROWID AS _id, (JORNADA || ' - ' || " +
                        "strftime('%d/%m/%Y',MIN(FECHA))) AS TEXTO " +
                        "FROM resultados " +
                        "WHERE TEMPORADA = ? " +
                        "GROUP BY TEMPORADA, JORNADA ORDER BY JORNADA DESC", filtro);
                break;
            case 1:
                c = db.rawQuery("SELECT ROWID AS _id, (JORNADA || ' - ' || " +
                        "strftime('%d/%m/%Y',MIN(FECHA))) AS TEXTO " +
                        "FROM resultados2 " +
                        "WHERE TEMPORADA = ? AND GRUPO = ? " +
                        "GROUP BY TEMPORADA, GRUPO, JORNADA ORDER BY JORNADA DESC", filtro);

                break;
        }
        return c;
    }

    public Cursor obtenerResultados(int div, String[] filtro) {
        //Filtro: Temporada, Grupo
        Cursor c = null;
        switch (div) {
            case 0:
                c = db.rawQuery("SELECT ROWID AS _id, " +
                                 "LOCAL, (GOLES_LOCAL || '  -  ' || GOLES_VISITANTE) " +
                                 "AS RESULTADO, VISITANTE " +
                                 "FROM resultados WHERE TEMPORADA = ? AND JORNADA = ?", filtro);
                break;
            case 1:
                c = db.rawQuery("SELECT ROWID AS _id, " +
                               "LOCAL, (GOLES_LOCAL || '  -  ' || GOLES_VISITANTE) " +
                               "AS RESULTADO, VISITANTE " +
                               "FROM resultados2 WHERE TEMPORADA = ? AND GRUPO = ? AND JORNADA = ?",
                               filtro);
                break;
        }
        return c;

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void onCreate(SQLiteDatabase db) {
    }
}

