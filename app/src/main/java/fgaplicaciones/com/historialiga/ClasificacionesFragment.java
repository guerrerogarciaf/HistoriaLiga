package fgaplicaciones.com.historialiga;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ClasificacionesFragment extends Fragment {

    private boolean noPrimeraVezTemp = false;
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
        view = inflater.inflate(R.layout.fragment_clasificaciones, container, false);
        bbdd = new LigaDBHelper(getActivity());
        rellenarSpinnerDivisiones();
        rellenarSpinnerTemporadas();
        rellenarClasificacion(0, null);
        listenerSpinnerDivision();
        listenerSpinnerTemporada();

        //Admob Banner
        adView = new AdView(getActivity());
        adView.setAdUnitId("ca-app-pub-2186608671196230/6023522505");
        adView.setAdSize(AdSize.SMART_BANNER);
        LinearLayout vista = (LinearLayout) view.findViewById(R.id.banner_principal);
        vista.addView(adView);
        if (Principal.prueba) {
            adRequestB = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
                    .addTestDevice("D4EFE24489A377D535C5C6C5D8DC553C") //
                    .build();
            adView.loadAd(adRequestB);
        }
        else {
            adRequestB = new AdRequest.Builder().build();
            adView.loadAd(adRequestB);
        }
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getActivity().setTitle(getResources().getText(R.string.clasificaciones));

    }

    private void mostrarIntersticial(){
        if (interstitial.isLoaded())
            interstitial.show();
    }


    private void rellenarSpinnerDivisiones() {

        //Divisione
        Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivision);

     //   Spinner spinnerDivision = (Spinner) findViewById(R.id.spinnerDivision);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterDivision = ArrayAdapter.createFromResource(getActivity(),
                R.array.divisiones, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterDivision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDivision.setAdapter(adapterDivision);
    }

    private void rellenarSpinnerTemporadas(){

        //Obtener Division
        Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivision);
        int div = spinnerDivision.getSelectedItemPosition();

        //Temporadas
        String[] from = new String[]{"TEXTO"};
        int[] to = new int[]{android.R.id.text1};
        // android.R.id.text1 is predefined UI element for Spinner control
        bbdd.abrir();
        Cursor cursor = bbdd.obtenerTemporadas(div);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, cursor, from, to);
        Spinner spinnerSeleccion = (Spinner) view.findViewById(R.id.spinnerSeleccion);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccion.setAdapter(cursorAdapter);
        bbdd.close();
    }
    public void rellenarClasificacion(int division, String[] filtro){

        //Obtener cursor
        bbdd.abrir();
        if (filtro == null)
            filtro = new String[]{Principal.temporadaDefecto};

        Cursor cursor = bbdd.obtenerClasificacion(division, filtro);
        // startManagingCursor(cursor);

        //List View Clasificaciones
        ListView listaCl = (ListView) view.findViewById(R.id.listViewCl);
        String[] fromCl = new String[]{"POSICION","EQUIPO","PT","PJ","PG","PE","PP","GF","GC"
                ,"PT_C","PJ_C","PG_C","PE_C","PP_C","GF_C","GC_C","PT_F","PJ_F","PG_F","PE_F","PP_F",
                "GF_F","GC_F"};
        int[] toCl = new int[]{R.id.textViewPos, R.id.textViewEquipo, R.id.textViewPT,
                R.id.textViewPJ, R.id.textViewPG, R.id.textViewPE, R.id.textViewPP, R.id.textViewGF,
                R.id.textViewGC, R.id.textViewPTCasa, R.id.textViewPJCasa, R.id.textViewPGCasa,
                R.id.textViewPECasa, R.id.textViewPPCasa,R.id.textViewGFCasa,R.id.textViewGCCasa,
                R.id.textViewPTFuera, R.id.textViewPJFuera, R.id.textViewPGFuera,
                R.id.textViewPEFuera, R.id.textViewPPFuera,R.id.textViewGFFuera,
                R.id.textViewGCFuera};
        SimpleCursorAdapter cursorAdapterCl = new SimpleCursorAdapter(getActivity(),
                R.layout.fila_clasificaciones, cursor, fromCl, toCl,0);
        listaCl.setAdapter(cursorAdapterCl);
        //Cerar base de datos
        bbdd.close();
    }

    private void listenerSpinnerDivision(){

        final Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivision);
        spinnerDivision.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View vista, int position, long id) {
                        if (noPrimeraVezDiv){
                            rellenarSpinnerTemporadas();
                        }
                        if (noPrimeraVezDiv == false)
                            noPrimeraVezDiv = true;

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }

    private void listenerSpinnerTemporada(){

        final Spinner spinnerSeleccion = (Spinner) view.findViewById(R.id.spinnerSeleccion);
        spinnerSeleccion.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View vista, int position, long id) {
                        /*
                        contador = contador + 1;
                         if (contador % 3 == 0)
                              mostrarIntersticial();
                        */
                        if (noPrimeraVezTemp){
                            Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivision);
                            int div = spinnerDivision.getSelectedItemPosition();

                            Cursor cursor = (Cursor) spinnerSeleccion.getSelectedItem();
                            switch (div) {
                                case 0:
                                    //Primera
                                    String [] texto1 = new String [1];
                                    texto1[0] = cursor.getString(cursor.getColumnIndex("TEXTO"));
                                    rellenarClasificacion(div, texto1);
                                    break;
                                case 1:
                                    //Segunda
                                    String [] texto2 = new String [2];
                                    String txt = cursor.getString(cursor.getColumnIndex("TEXTO"));
                                    texto2[0] = txt.substring(0,7);
                                    texto2[1] = "1";
                                    if (txt.length() > 7 && txt.charAt(12) == '2')
                                        texto2[1] = "2";
                                    rellenarClasificacion(div, texto2);
                                    break;
                            }
                        }

                        if (noPrimeraVezTemp == false)
                            noPrimeraVezTemp = true;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }


}
