package fgaplicaciones.com.historialiga;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Principal extends AppCompatActivity

{
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private Toolbar toolbar;
    private static final String TAG = "HistoriaLiga";
   //Constantes
    final static String temporadaDefecto = "2016-17";
    final static String jornadaDefecto = "1";
   //Navigation Drawer
    private String[] opciones;
    private DrawerLayout drawerLayout;
    private ListView listaDrawer;
    private ActionBarDrawerToggle drawerToggle;
    int contador;
   //Modo prueba
    static boolean prueba = false;
    CognitoCachingCredentialsProvider credentialsProvider = null;
    AmazonS3 s3 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MEJORA: HACER ESTA COMPROBACION SOLO UNA VEZ!!
        if (savedInstanceState == null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            // Crear base de datos si hay cambio de versión de app (y la primera vez)
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "Error al obtener nombre de paquete");
            }
            int versionApp = pInfo.versionCode;
            int versionBd = prefs.getInt("versionBd", 0);
            if (!(versionBd == versionApp)) {
                LigaDBHelper bbdd = new LigaDBHelper(this);
                try {
                    bbdd.crear(false);
                } catch (IOException e) {
                    Log.d(TAG, "Error al crear base de datos");
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("versionBd", versionApp);
                editor.commit();
            }
        }

        setContentView(R.layout.activity_clasificaciones);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

     //Navigation Drawer
        opciones = getResources().getStringArray(R.array.navigation_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Toggle

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_abierto,R.string.drawer_cerrado) {

            /** Called when a drawer has settled in a completely open state. */
           @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            //    getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
           //     getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);

        //Fin Toggle
        listaDrawer = (ListView) findViewById(R.id.drawer_lista);

        // Set the adapter for the list view
        listaDrawer.setAdapter(new ArrayAdapter<String>(this,
                R.layout.fila_drawer, opciones));

        // Set the list's click listener

        listaDrawer.setOnItemClickListener(new ListView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
            }

            /**
             * Swaps fragments in the main content view
             */
            private void selectItem(int position) {

                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new ClasificacionesFragment();
                        break;
                    case 1:
                        fragment = new ResultadosFragment();
                        break;
                    case 2:
                        fragment = new GanadoresFragment();
                        break;
                    case 3:
                        fragment = new GoleadoresFragment();
                        break;
                    case 4:
                        fragment = new CopaFragment();
                        break;
                }

                LinearLayout drawerMarco = (LinearLayout) findViewById(R.id.drawer_marco);

                Bundle args = new Bundle();
                args.putInt("ARG_POS_DRAWER", position);
                fragment.setArguments(args);
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment,
                                Integer.toString(fragmentManager.getBackStackEntryCount()))
                        .addToBackStack(null)
                        .commit();
       //        fragmentManager.executePendingTransactions();
                // Highlight the selected item, update the title, and close the drawer
                listaDrawer.setItemChecked(position, true);
                //      toolbar.setTitle(opciones[position]);
                drawerLayout.closeDrawer(drawerMarco);
            }
        });

        //Cargar Fragment Clasificaciones al inicio
        FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ClasificacionesFragment fragment = new ClasificacionesFragment();
            fragmentTransaction.add(R.id.fragment_container, fragment,
                    Integer.toString(fragmentManager.getBackStackEntryCount()));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
    //      fragmentManager.executePendingTransactions();

        }


    // Initialize the Amazon Cognito credentials provider
    credentialsProvider = new CognitoCachingCredentialsProvider(
            getApplicationContext(),
            "eu-west-1:a558502b-1c07-4b37-8fa0-50eebefd4634",
            Regions.EU_WEST_1 // Region
    );
    // Create an S3 client
    // Hacerlo aquí para que sea una strong reference
    s3 = new AmazonS3Client(credentialsProvider);
    }

    protected Fragment getCurrentFragment() {
        return getFragmentAt(getFragmentCount() - 1);
    }

    protected int getFragmentCount() {
        return getFragmentManager().getBackStackEntryCount();
    }

    private Fragment getFragmentAt(int index) {
        return getFragmentCount() > 0 ? getFragmentManager().
                findFragmentByTag(Integer.toString(index)) : null;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().commit();
        }
        else
            super.onBackPressed();
    }
    @Override
    public void setTitle(CharSequence title) {

        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clasificaciones, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_compartir:
                intentCompartir();
                return true;
            case R.id.action_puntuar:
                intentPuntuar();
                return true;

            case R.id.action_actualizar:
                actualizarDatos();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void intentCompartir(){
        Intent enviarIntent = new Intent();
        enviarIntent.setAction(Intent.ACTION_SEND);
        enviarIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.compartir_app));
        enviarIntent.setType("text/plain");
        startActivity(Intent.createChooser(enviarIntent,
                getResources().getText(R.string.recomendar)));
    }
    private void intentPuntuar(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=foo"));

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        if (!list.isEmpty()){
            Intent i = new Intent(Intent.ACTION_VIEW);
            //Para desactivar el backstack del Market y que se vuelve a la aplicación
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                 Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            i.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(i);
        }
    }

    private void actualizarDatos()
    {
        final Context contexto = getApplicationContext();
        final ProgressDialog progreso;
        // Set the region of your S3 bucket
      //  s3.setRegion(Region.getRegion(Regions.DEFAULT_REGION));
        //Instanciar transferUtility
        TransferUtility transferUtility = new TransferUtility(s3, contexto);
        //Crear fichero temporal
        File fichero = new File(contexto.getFilesDir(), "Liga.sqlite");
        //Descarga instanciando transferObserver
        TransferObserver observer = transferUtility.download(
                "futbolespanol",  //   The bucket to download from
                "Liga",   //  The key for the object to download
                fichero  // The file to download the object to
        );
        //Contexto de actividad en vez de aplicación
        progreso = new ProgressDialog(Principal.this);
        progreso.setCancelable(true);
        progreso.setMessage(getResources().getText(R.string.descargando));
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setIndeterminate(true);
        progreso.show();

        observer.setTransferListener
                (new TransferListener() {
                     @Override
                     public void onStateChanged(int id, TransferState estado) {
                         Log.d(TAG, "Cambio de estado a " + estado.toString());
                         switch (estado) {
                             case WAITING:
                                 break;
                             case IN_PROGRESS:
                                 break;
                             case COMPLETED:
                                 Log.d(TAG, "Descarga terminada");
                                 progreso.dismiss();
                                 Toast toast = Toast.makeText(contexto,
                                         getResources().getText(R.string.completada),
                                         Toast.LENGTH_SHORT);
                                 toast.show();
                                 copiarBD();
                                 refrescarFragments();
                                 break;
                             case CANCELED:
                                 Log.d(TAG, "Descarga cancelada");
                                 progreso.dismiss();
                                 break;
                             case FAILED:
                                 Log.d(TAG, "Error en la descarga");
                                 progreso.dismiss();
                                 break;
                         }
                     }

                     @Override
                     public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                         // Do something in the callback
                     }

                     @Override
                     public void onError(int id, Exception e) {
                         progreso.dismiss();
                         Log.d(TAG, "Error en la transferencia:  " + e.toString());
                         Toast toast = Toast.makeText(contexto,
                                 getResources().getText(R.string.error_descarga),
                                 Toast.LENGTH_SHORT);
                         toast.show();
                     }
                 }
        );
        }

    private void copiarBD(){
        LigaDBHelper bbdd = new LigaDBHelper(this);
        try {
            bbdd.crear(true);
        } catch (IOException e) {
            Log.d(TAG, "Error al crear base de datos");
        }
    }

    private void refrescarFragments () {
        Fragment fr = null;
        FragmentManager fm = getFragmentManager();
        int numFrag = fm.getBackStackEntryCount() - 1;
        Log.d(TAG, "numFrag: " + Integer.toString(numFrag));
    //    for (int i = 0; i <= 0; i++) {
            fr = fm.findFragmentByTag(Integer.toString(numFrag));
                resetFragment(fr);
   //     }
    }

    private void resetFragment(Fragment frag){
        getFragmentManager()
                .beginTransaction()
                .detach(frag)
                .attach(frag)
                .commit();
           getFragmentManager().executePendingTransactions();
        }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

}
