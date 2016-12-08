package fgaplicaciones.com.historialiga;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class GoleadoresFragment extends Fragment {

    private boolean noPrimeraVezDiv = false;
    private LigaDBHelper bbdd;
    //Variables AdMob
    private AdView adView;
    private  AdRequest adRequestI;
    private  AdRequest adRequestB;
    private InterstitialAd interstitial;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_goleadores, container, false);
        bbdd = new LigaDBHelper(getActivity());
        rellenarGoleadores();
        //Admob Banner
        adView = new AdView(getActivity());
        adView.setAdUnitId("ca-app-pub-2186608671196230/6023522505");
        adView.setAdSize(AdSize.SMART_BANNER);

        LinearLayout vista = (LinearLayout)view.findViewById(R.id.banner_goleadores);
        vista.addView(adView);
        if (Principal.prueba) {
            adRequestB = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
                    .addTestDevice("D4EFE24489A377D535C5C6C5D8DC553C") //
                    .build();
            adView.loadAd(adRequestB);
        }
        else{
            adRequestB = new AdRequest.Builder().build();
            adView.loadAd(adRequestB);
        }
        return view;
    }
    private void mostrarIntersticial(){
        if (interstitial.isLoaded())
            interstitial.show();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getActivity().setTitle(getResources().getText(R.string.goleadores));

    }

    public void rellenarGoleadores(){

        //Obtener cursor

        bbdd.abrir();

        Cursor cursor = bbdd.obtenerGoleadores();
        // startManagingCursor(cursor);

        //List View Clasificaciones
        ListView listaGl = (ListView) view.findViewById(R.id.listViewGl);
        String[] fromGl = new String[]{"TEMPORADA","JUGADOR","EQUIPO","GOLES","PROMEDIO"};
        int[] toGl = new int[]{R.id.textViewTemporadaGl, R.id.textViewJugadorGl,
                R.id.textViewEquipoGl,R.id.textViewGolesGl,R.id.textViewMediaGl};
        SimpleCursorAdapter cursorAdapterGl = new SimpleCursorAdapter(getActivity(),
                R.layout.fila_goleadores, cursor, fromGl, toGl, 0);
        listaGl.setAdapter(cursorAdapterGl);
        //Cerar base de datos
        bbdd.close();
    }

}


