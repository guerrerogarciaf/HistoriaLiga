package fgaplicaciones.com.historialiga;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class CopaFragment extends Fragment {

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
        view = inflater.inflate(R.layout.fragment_copa, container, false);
        bbdd = new LigaDBHelper(getActivity());
        rellenarCopa();
        //Admob Banner
        adView = new AdView(getActivity());
        adView.setAdUnitId("ca-app-pub-2186608671196230/6023522505");
        adView.setAdSize(AdSize.SMART_BANNER);

        LinearLayout vista = (LinearLayout)view.findViewById(R.id.banner_copa);
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


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getActivity().setTitle(getResources().getText(R.string.copa));

    }

    public void rellenarCopa(){

        //Obtener cursor

        bbdd.abrir();

        Cursor cursor = bbdd.obtenerCopa();
        // startManagingCursor(cursor);

        //List View Clasificaciones
        ListView listaCp = (ListView) view.findViewById(R.id.listViewCp);
        String[] fromCp = new String[]{"TEMPORADA","CAMPEON","RESULTADO","SUBCAMPEON"};
        int[] toCp = new int[]{R.id.textViewTemporadaCp, R.id.textViewGanadorCp,
                R.id.textViewResultadoCp,R.id.textViewFinalistaCp};
        SimpleCursorAdapter cursorAdapterCp = new SimpleCursorAdapter(getActivity(),
                R.layout.fila_copa, cursor, fromCp, toCp, 0);
        listaCp.setAdapter(cursorAdapterCp);
        //Cerar base de datos
        bbdd.close();
    }

}


