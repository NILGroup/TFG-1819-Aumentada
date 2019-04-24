package ucm.fdi.tfg.frases;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.conexionServidor.ConexionPICTAR;

public class FrasesPictosActivity extends AppCompatActivity {

    private GridView gridView_pictos;
    private GridViewAdapter gridViewAdapter;

    private String texto_pictos;
    private boolean mayus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frases_pictos);

        gridView_pictos = (GridView) findViewById(R.id.gridView_pictos);

        // Recibir el String con las frases desde FrasesActivity
        texto_pictos = getIntent().getStringExtra(Variables.FRASES);
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);

        // Establece la conexion con el servidor y devuelve el resultado con los pictos.
        ArrayList<ArrayList<String>> result = estableceConexionPictar(texto_pictos);

        // Crea los grids para los pictos
        gridViewAdapter = new GridViewAdapter(result);
        gridView_pictos.setAdapter(gridViewAdapter);

    }


    /**
     * Dado el texto recibido del Activity Frases, establece la conexion con el servidor
     * PICTAR para recibir los id de los pictogramas.
     * @param texto Texto a procesar.
     * @return Devuelve un ArrayList con dichos ids.
     */
    private ArrayList<ArrayList<String>> estableceConexionPictar(String texto) {

        // Establecer la conexion con el servidor PICTAR
        ConexionPICTAR conexionPICTAR = new ConexionPICTAR(texto);
        conexionPICTAR.start();

        try {
            conexionPICTAR.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         return conexionPICTAR.getResultado();
    }



    private class GridViewAdapter extends BaseAdapter {

        // Array list con todos los pictogramas
        private ArrayList<ArrayList<String>> arrayList_pictos;
        private LayoutInflater layoutInflater;
        // Un array que llevara la cuenta del pictograma que esta saliendo por pantalla
        private int[] pictos_seleccionados;


        private GridViewAdapter(ArrayList<ArrayList<String>> arrayList) {
            super();
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.arrayList_pictos = arrayList;
            this.pictos_seleccionados = new int[this.arrayList_pictos.size()];
            for (int i = 0; i < pictos_seleccionados.length; i++) {
                pictos_seleccionados[i] = 1;
            }
        }

        @Override
        public int getCount() {
            return arrayList_pictos.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList_pictos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = layoutInflater.inflate(R.layout.pictos, null);
            final ViewHolderGrid holder = new ViewHolderGrid();

            // IMAGEN !!!!!!!!!
            holder.imageView = convertView.findViewById(R.id.imageView_pictos);
            // Si pulsamos la imagen
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // AQUI CAMBIAMOS EL PICTO!!!!!!
                    // El array siempre tendra 2 elementos. Por tanto si hay 3 o mas elementos
                    // significa que tendrá mas de 1 pictograma
                    if (arrayList_pictos.get(position).size() > 2) {
                        if (pictos_seleccionados[position] < arrayList_pictos.get(position).size() - 1) {
                            pictos_seleccionados[position] += 1;
                        }
                        else {
                            pictos_seleccionados[position] = 1;
                        }
                        Picasso.get()
                                .load(Variables.PICTAR_PICTOGRAMAS +
                                        arrayList_pictos.get(position).get(pictos_seleccionados[position]))
                                .into(holder.imageView);
                    }

                }
            });
            // Poner imagen
            if (arrayList_pictos.get(position).get(1).equals(Variables.PICTOS_NOT_FOUND)) {
                // Si la palabra no tiene pictogramas. Pone una imagen -> una x roja
                holder.imageView.setImageResource(R.drawable.pictos_not_found);
            }else {
                // Si la palabra o frase tiene pictograma,
                // selecciona el primer pictograma del array
                Picasso.get()
                    .load(Variables.PICTAR_PICTOGRAMAS +
                            arrayList_pictos.get(position).get(pictos_seleccionados[position]))
                    .into(holder.imageView);
            }


            // TEXTO DE LA IMAGEN !!!!!!!!
            holder.textView = convertView.findViewById(R.id.textView_pictos);
            // Poner nombre de picto
            if (mayus) {
                holder.textView.setText(arrayList_pictos.get(position).get(0).toUpperCase());
            }
            else {
                holder.textView.setText(arrayList_pictos.get(position).get(0).toLowerCase());
            }

            convertView.setTag(holder);

            return convertView;
        }
    }

    public static class ViewHolderGrid {
        ImageView imageView;
        TextView textView;
    }
}
