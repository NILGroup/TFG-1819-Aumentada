package ucm.fdi.tfg.frases;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.conexionServidor.ConexionSpacy;
import ucm.fdi.tfg.palabras.PalabrasActivity;


public class FrasesActivity extends AppCompatActivity {

    // Para crear los checkBox con las frases.
    private CheckListAdapter checkListAdapter;
    private ListView listView_frases;


    private String texto_frases;
    private boolean mayus;

    // Las frases que se han seleccioonado
    private boolean[] checkBox_seleccionados;


    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;
    private ArrayList<Integer> elementos = new ArrayList<>();



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_frases);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Para el Menu
        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];


        // Botones que aparecen en la vista
        Button button_pictogramas = findViewById(R.id.button_pictogramas_frases);
        Button button_palabra     = findViewById(R.id.button_palabra_frases);

        // ListView para añadir las frases.
        listView_frases = findViewById(R.id.listView_frases);


        // Texto
        texto_frases = getIntent().getStringExtra(Variables.FRASES);
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);

        // PRUEBAS
        //String texto_frases = "Google Cloud Vision API realiza un análisis de diseño en la imagen para segmentar la ubicación del texto. Una vez que se detecta la ubicación general, el módulo OCR realiza un análisis de reconocimiento de texto en la ubicación especificada para generar el texto. Finalmente, los errores se corrigen en un paso de procesamiento posterior introduciéndolos a través de un modelo de idioma o diccionario. Todo esto se realiza a través de una red neuronal convolucional en la que cada neurona solo está conectada a un subconjunto de neuronas en cada capa. Las redes neuronales convolucionales son un subconjunto de redes neuronales y pretenden imitar la estructura jerárquica de nuestra corteza visual en la forma en que identificamos los objetos.";
        //String texto_frases = "Caperucita Roja vivía en el bosque. Le gustaba saltar a la pata coja. Se comía su comida y le hacía caso a su mamá";


        // Rellenar los check box con las frases. True porque se inicializa por primera vez
        fillList(true);


        // ******** PASAR A PICTOS ********
        button_pictogramas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean seleccionado = false;
                for (boolean ok: checkBox_seleccionados) {
                    if (ok) {
                        pulsarBotonPictogramas();
                        seleccionado = true;
                    }
                }
                if (!seleccionado) {
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Selecciona una frase", Toast.LENGTH_SHORT);
                    toast1.show();
                }

            }
        });


        // ******** PASAR A PALABRA ********
        button_palabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean seleccionado = false;
                for (boolean ok: checkBox_seleccionados) {
                    if (ok) {
                        pulsarBotonPalabras();
                        seleccionado = true;
                    }
                }
                if (!seleccionado) {
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Selecciona una frase", Toast.LENGTH_SHORT);
                    toast1.show();
                }
            }
        });

    }

    /**
     *  Boton de Pictogramas
     *  Pasará al Activity Pictos.
     */
    private void pulsarBotonPictogramas() {
        Intent intent = new Intent(this, FrasesPictosActivity.class);
        intent.putExtra(Variables.FRASES, checkListAdapter.getFrasesSeleccionadas());
        intent.putExtra(Variables.MAYUS, mayus);
        startActivity(intent);
    }

    private void pulsarBotonPalabras() {
        Intent intent = new Intent(this, PalabrasActivity.class);
        intent.putExtra(Variables.FRASES, checkListAdapter.getFrasesSeleccionadas());
        intent.putExtra(Variables.MAYUS, mayus);
        startActivity(intent);
    }




    /**
     * Rellenar los check box con cada una de las frases
     * @param ini Se pasa un booleano para saber si es la primera vez que
     *            se iniciliza la lista. Esto se hace porque al cambiar de mayusculas
     *            a minusculas se perdían las frases que se habian marcado.
     */
    private void fillList(boolean ini) {

        checkListAdapter = new CheckListAdapter();

        // Llamamos al servicio Spacy para que devuelva las oraciones
        try {
            ConexionSpacy conexionSpacy = new ConexionSpacy(this, texto_frases, "texto", "oraciones");
            conexionSpacy.start();
            conexionSpacy.join();
            ArrayList<String> oraciones = conexionSpacy.getOraciones();

            for(String f : oraciones) {
                checkListAdapter.addFrase(f.trim());
            }
            checkListAdapter.iniSeleccionadas(ini);
            listView_frases.setAdapter(checkListAdapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private class CheckListAdapter extends BaseAdapter {

        private ArrayList<String> frases = new ArrayList<>();
        private LayoutInflater layoutInflater;

        CheckListAdapter() {
            super();
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void addFrase(String frase){
            frases.add(frase);
            notifyDataSetChanged();
        }

        void iniSeleccionadas(boolean ini) {
            if (ini) {
                checkBox_seleccionados = new boolean[frases.size()];
            }
        }


        String getFrasesSeleccionadas() {

            StringBuilder frases_seleccionadas = new StringBuilder();

            for (int i = 0; i < checkBox_seleccionados.length; i++) {
                if (checkBox_seleccionados[i]) {
                    frases_seleccionadas.append(frases.get(i));
                }
            }

            return frases_seleccionadas.toString();
        }

        @Override
        public int getCount() {
            return frases.size();
        }

        @Override
        public Object getItem(int position) {
            return frases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.fila_frases, null);
            final ViewHolder holder = new ViewHolder();
            holder.chkItem = convertView.findViewById(R.id.check_frases);
            holder.chkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Array de seleccionadas, cuando se selecciona una frase se actualiza
                    checkBox_seleccionados[position] = holder.chkItem.isChecked();
                }
            });

            // Espacio entre el cuadradito del check box y el texto
            holder.chkItem.setPadding(holder.chkItem.getPaddingLeft() + (int)(30.5f),
                    holder.chkItem.getPaddingTop(),
                    holder.chkItem.getPaddingRight(),
                    holder.chkItem.getPaddingBottom());

            holder.chkItem.setChecked(checkBox_seleccionados[position]);
            convertView.setTag(holder);
            holder.chkItem.setText((String)getItem(position));

            return convertView;
        }

    }

    public static class ViewHolder {
        CheckBox chkItem;
    }










    /****************     MENU     *******************/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_ajustes:
                pulsarBotonAjustes();
                break;
            case R.id.item_about_us:
                pulsarBotonAboutUs();
                break;
            case R.id.item_mayus:
                pulsarItemMayus();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pulsarItemMayus(){
        // Cambia mayusculas o minusculas segun pulsemos el boton
        if (mayus) {
            mayus = false;
            texto_frases = texto_frases.toLowerCase();
        }
        else {
            mayus = true;
            texto_frases = texto_frases.toUpperCase();
        }
        fillList(false);
    }

    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(FrasesActivity.this);
        adBuilder.setTitle("LeeFácil");
        adBuilder.setMessage("HOLAAA");
        adBuilder.setCancelable(false);
        adBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = adBuilder.create();
        ad.show();
    }



    private void pulsarBotonAjustes() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(FrasesActivity.this);
        adBuilder.setTitle("SELECCIONA LAS OPCIONES");
        adBuilder.setMultiChoiceItems(elementos_menu, elementos_seleccionados, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    if (!elementos.contains(which)){
                        elementos.add(which);
                    }
                    else {
                        elementos.remove(which);
                    }
                }
            }
        });
        adBuilder.setCancelable(false);
        adBuilder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        adBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.setNeutralButton("SELECCIONAR TODO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i = 0; i < elementos_seleccionados.length; i++) {
                    elementos_seleccionados[i] = true;
                }
            }
        });
        AlertDialog ad = adBuilder.create();
        ad.show();
    }

}
