package ucm.fdi.tfg.frases;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ListView;

import java.util.ArrayList;

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.TextoPlanoActivity;
import ucm.fdi.tfg.TextoResumenActivity;

public class FrasesActivity extends AppCompatActivity{

    public final static String FRASES_SELECCIONADAS = "FRASES";
    public final static String MAYUS = "MAYUS";

    private Button button_pictogramas;
    private Button button_palabras;

    private CheckListAdapter checkListAdapter;
    private ListView listView_frases;

    private boolean mayus;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_frases);

        button_pictogramas = findViewById(R.id.button_pictogramas);
        button_palabras    = findViewById(R.id.button_palabras);

        listView_frases = findViewById(R.id.lista_frase);

        // Texto capturado
        final String texto = getIntent().getStringExtra(TextoPlanoActivity.FRASES);
        mayus = getIntent().getBooleanExtra(TextoResumenActivity.MAYUS, false);

        //String texto = "Google Cloud Vision API realiza un análisis de diseño en la imagen para segmentar la ubicación del texto. Una vez que se detecta la ubicación general, el módulo OCR realiza un análisis de reconocimiento de texto en la ubicación especificada para generar el texto. Finalmente, los errores se corrigen en un paso de procesamiento posterior introduciéndolos a través de un modelo de idioma o diccionario. Todo esto se realiza a través de una red neuronal convolucional en la que cada neurona solo está conectada a un subconjunto de neuronas en cada capa. Las redes neuronales convolucionales son un subconjunto de redes neuronales y pretenden imitar la estructura jerárquica de nuestra corteza visual en la forma en que identificamos los objetos.";
        //String texto = "Caperucita Roja vivía en el bosque. Le gustaba saltar a la pata coja. Se comía su comida y le hacía caso a su mamá";

        // Rellenar los check box con las frases
        fillList(texto);

        // ******** PASAR A PICTOS ********
        button_pictogramas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsarBotonPictogramas();
            }
        });

    }

    /**
     *  Boton de Pictogramas
     *  Pasará al Activity Pictos.
     */
    private void pulsarBotonPictogramas() {
        Intent intent = new Intent(this, PictosActivity.class);
        intent.putExtra(FrasesActivity.FRASES_SELECCIONADAS, checkListAdapter.getFrasesSeleccionadas());
        intent.putExtra(FrasesActivity.MAYUS, mayus);
        startActivity(intent);
    }



    /**
     * Rellenar los check box con cada una de las frases
     * @param texto Se pasa el texto completo, y se dividirá por frases
     *              para mostrarlo.
     */
    private void fillList(String texto) {
        checkListAdapter = new CheckListAdapter();
        for(String f : texto.split("\\.")) {
            checkListAdapter.addFrase(f);
        }
        checkListAdapter.iniSeleccionadas();
        listView_frases.setAdapter(checkListAdapter);
    }


    private class CheckListAdapter extends BaseAdapter {

        private ArrayList<String> frases = new ArrayList<String>();
        private LayoutInflater layoutInflater;
        private boolean[] seleccionadas;

        CheckListAdapter() {
            super();
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void addFrase(String frase){
            frases.add(frase);
            notifyDataSetChanged();
        }

        void iniSeleccionadas() {
            seleccionadas = new boolean[frases.size()];
        }


        String getFrasesSeleccionadas() {

            String frases_seleccionadas = "";

            for (int i = 0; i < seleccionadas.length; i++) {
                if (seleccionadas[i]) {
                    frases_seleccionadas += frases.get(i);
                }
            }

            return frases_seleccionadas;
        }

        @Override
        public int getCount() {
            return frases.size();
        }

        @Override
        public Object getItem(int position) {
            return frases.get(position).toString();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.fila, null);
            final ViewHolder holder = new ViewHolder();
            holder.chkItem = (CheckBox) convertView.findViewById(R.id.check_frases);
            holder.chkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Array de seleccionadas, cuando se selecciona una frase se actualiza
                    seleccionadas[position] = holder.chkItem.isChecked();
                }
            });

            // Espacio entre el cuadradito del check box y el texto
            holder.chkItem.setPadding(holder.chkItem.getPaddingLeft() + (int)(30.5f),
                    holder.chkItem.getPaddingTop(),
                    holder.chkItem.getPaddingRight(),
                    holder.chkItem.getPaddingBottom());

            holder.chkItem.setChecked(seleccionadas[position]);
            convertView.setTag(holder);
            holder.chkItem.setText((String)getItem(position));
            return convertView;
        }

    }


    public static class ViewHolder {
        CheckBox chkItem;
    }


}
