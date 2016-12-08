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

public class ResultadosFragment extends Fragment {

    private boolean noPrimeraVezTemp = false;
    private boolean noPrimeraVezDiv = false;
    private boolean noPrimeraVezJor = false;
    private LigaDBHelper bbdd;
    //Variables AdMob
    private AdView adView;
    private  AdRequest adRequestB;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_resultados, container, false);
        bbdd = new LigaDBHelper(getActivity());
        rellenarSpinnerDivisiones();
        rellenarSpinnerTemporadas();
        rellenarSpinnerJornadas(0, null);
     //   rellenarResultados(0, null);  --> No hacen falta switches primera vez, quitarlos
        listenerSpinnerDivisiones();
        listenerSpinnerTemporadas();
        listenerSpinnerJornadas();
        //Admob Banner
        adView = new AdView(getActivity());
        adView.setAdUnitId("ca-app-pub-2186608671196230/6023522505");
        adView.setAdSize(AdSize.SMART_BANNER);
        LinearLayout vista = (LinearLayout)view.findViewById(R.id.banner_principal);
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
        getActivity().setTitle(getResources().getText(R.string.resultados));

    }


    private void rellenarSpinnerDivisiones() {

        //Divisione
        Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivisionesRe);

        ArrayAdapter<CharSequence> adapterDivision = ArrayAdapter.createFromResource(getActivity(),
                R.array.divisiones, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterDivision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDivision.setAdapter(adapterDivision);
    }

    private void rellenarSpinnerTemporadas(){

        //Obtener Division
        Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivisionesRe);
        int div = spinnerDivision.getSelectedItemPosition();

        //Temporadas
        String[] from = new String[]{"TEXTO"};
        int[] to = new int[]{android.R.id.text1};
        // android.R.id.text1 is predefined UI element for Spinner control
        bbdd.abrir();
        Cursor cursor = bbdd.obtenerTemporadasRe(div);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, cursor, from, to);
        Spinner spinnerTemporadas = (Spinner) view.findViewById(R.id.spinnerTemporadasRe);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemporadas.setAdapter(cursorAdapter);
        bbdd.close();
    }

    private void rellenarSpinnerJornadas(int division, String[] filtro ){

        if (filtro == null)
            filtro = new String[]{Principal.temporadaDefecto};

        //Obtener Division
        Spinner spinnerJornadas = (Spinner) view.findViewById(R.id.spinnerJornadasRe);


        //Temporadas
        String[] from = new String[]{"TEXTO"};
        int[] to = new int[]{android.R.id.text1};
        // android.R.id.text1 is predefined UI element for Spinner control
        bbdd.abrir();
        Cursor cursor = bbdd.obtenerJornadas(division, filtro);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, cursor, from, to, 0);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJornadas.setAdapter(cursorAdapter);
        bbdd.close();
    }

    public void rellenarResultados(int division, String[] filtro){

        //Obtener cursor

        bbdd.abrir();
        if (filtro == null)
            filtro = new String[]{Principal.temporadaDefecto, Principal.jornadaDefecto};

        Cursor cursor = bbdd.obtenerResultados(division, filtro);
        // startManagingCursor(cursor);

        //List View Clasificaciones
        ListView listaRe = (ListView) view.findViewById(R.id.listViewRe);
        String[] fromRe = new String[]{"LOCAL","RESULTADO", "VISITANTE"};
        int[] toRe = new int[]{R.id.textViewLocalRe, R.id.textViewResultadoRe,
                R.id.textViewGolesVisitanteRe};
        SimpleCursorAdapter cursorAdapterCl = new SimpleCursorAdapter(getActivity(),
                R.layout.fila_resultados, cursor, fromRe, toRe);
        listaRe.setAdapter(cursorAdapterCl);
        //Cerar base de datos
        bbdd.close();
    }


    private void listenerSpinnerDivisiones(){

        final Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivisionesRe);
        spinnerDivision.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View vista, int position, long id) {
                         if (noPrimeraVezDiv){
                            rellenarSpinnerTemporadas();
                         }
                        if (!noPrimeraVezDiv)
                            noPrimeraVezDiv = true;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }

    private void listenerSpinnerTemporadas(){

        final Spinner spinnerTemporadas = (Spinner) view.findViewById(R.id.spinnerTemporadasRe);
        spinnerTemporadas.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View vista, int position, long id) {
                        /*
                        contador = contador + 1;
                         if (contador % 3 == 0)
                              mostrarIntersticial();
                        */
                        Cursor cursor = (Cursor) spinnerTemporadas.getSelectedItem();
                        if (noPrimeraVezTemp){
                            final Spinner spinnerDivision = (Spinner) view.findViewById
                                    (R.id.spinnerDivisionesRe);
                            int division = spinnerDivision.getSelectedItemPosition();


                            switch (division) {
                                case 0:
                                    //Primera
                                    String [] temporada1 = new String [1];
                                    temporada1[0] = cursor.getString(cursor.getColumnIndex("TEXTO"));
                                    rellenarSpinnerJornadas(division, temporada1);
                                    break;
                                case 1:
                                    //Segunda
                                    String [] temporada2 = new String [2];
                                    String txt = cursor.getString(cursor.getColumnIndex("TEXTO"));
                                    temporada2[0] = txt.substring(0,7);
                                    temporada2[1] = "1";
                                    if (txt.length() > 7 && txt.charAt(12) == '2')
                                        temporada2[1] = "2";
                                    rellenarSpinnerJornadas(division, temporada2);
                                    break;
                            }
                         }

                        if (!noPrimeraVezTemp)
                            noPrimeraVezTemp = true;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }
    private void listenerSpinnerJornadas(){
    //Spinners
        final Spinner spinnerTemporadas = (Spinner) view.findViewById(R.id.spinnerTemporadasRe);
        final Spinner spinnerDivision = (Spinner) view.findViewById(R.id.spinnerDivisionesRe);
        final Spinner spinnerJornadas = (Spinner) view.findViewById(R.id.spinnerJornadasRe);

        spinnerJornadas.setOnItemSelectedListener(


                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(

                            AdapterView<?> parent, View vista, int position, long id) {
                         final Cursor cursorTemp = (Cursor) spinnerTemporadas.getSelectedItem();
                         final Cursor cursorJor = (Cursor) spinnerJornadas.getSelectedItem();
                         final int division = spinnerDivision.getSelectedItemPosition();
                         String txt;
                         String [] parametros1;
                         String [] parametros2;
        //                 if (noPrimeraVezJor){

                            switch (division) {
                                case 0:
                                    //Primera
                                    parametros1 = new String [2];
                                    parametros1[0] = cursorTemp
                                            .getString(cursorTemp.getColumnIndex("TEXTO"));

                                    txt = cursorJor
                                            .getString(cursorJor.getColumnIndex("TEXTO"));
                                    parametros1[1] = txt.substring(0,2);

                                    rellenarResultados(division, parametros1);
                                    break;
                                case 1:
                                    //Segunda
                                    parametros2 = new String [3];
                                    txt = cursorTemp.getString(cursorTemp.getColumnIndex("TEXTO"));
                                    parametros2[0] = txt.substring(0,7);
                                    parametros2[1] = "1";
                                    if (txt.length() > 7 && txt.charAt(12) == '2')
                                        parametros2[1] = "2";
                                    txt = cursorJor
                                            .getString(cursorJor.getColumnIndex("TEXTO"));
                                    parametros2[2] = txt.substring(0,2);
                                    rellenarResultados(division, parametros2);
                                    break;
                            }
           //              }

           //             else {
           //                  noPrimeraVezJor = true;
           //              }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }

}
