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

public class GanadoresFragment extends Fragment {

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
        view = inflater.inflate(R.layout.fragment_ganadores, container, false);
        bbdd = new LigaDBHelper(getActivity());
        rellenarSpinnerDivisiones();
        listenerSpinnerDivision();

        //Admob Banner
        adView = new AdView(getActivity());
        adView.setAdUnitId("ca-app-pub-2186608671196230/6023522505");
        adView.setAdSize(AdSize.SMART_BANNER);
        LinearLayout vista = (LinearLayout)view.findViewById(R.id.banner_ganadores);
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
        getActivity().setTitle(getResources().getText(R.string.campeones));

    }


    private void rellenarSpinnerDivisiones() {

        //Divisiones
        Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivisionGn);

     //   Spinner spinnerDivision = (Spinner) findViewById(R.id.spinnerDivision);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterDivision = ArrayAdapter.createFromResource(getActivity(),
                R.array.divisiones, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterDivision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDivision.setAdapter(adapterDivision);
    }
    public void rellenarGanadores(int division){

        //Obtener cursor

        bbdd.abrir();

        Cursor cursor = bbdd.obtenerGanadores(division);
        // startManagingCursor(cursor);

        //List View Clasificaciones
        ListView listaGn = (ListView) view.findViewById(R.id.listViewGn);
        String[] fromGn = new String[]{"TEXTO","EQUIPO"};
        int[] toGn = new int[]{R.id.textViewTemporada, R.id.textViewGanador};
        SimpleCursorAdapter cursorAdapterGn = new SimpleCursorAdapter(getActivity(),
                R.layout.fila_ganadores, cursor, fromGn, toGn, 0);
        listaGn.setAdapter(cursorAdapterGn);
        //Cerar base de datos
        bbdd.close();
    }

    private void listenerSpinnerDivision(){

        final Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivisionGn);
        spinnerDivision.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View vista, int position, long id) {

                            rellenarGanadores(position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }




}
