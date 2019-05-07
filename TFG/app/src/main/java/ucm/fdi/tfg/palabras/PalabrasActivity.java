package ucm.fdi.tfg.palabras;

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
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.conexionServidor.ConexionSpacy;


public class PalabrasActivity extends AppCompatActivity {

    GridView gridView_palabras;

    private ArrayList<ArrayList<String>> palabras;

    private String texto_palabras;
    private boolean mayus;

    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;
    private ArrayList<Integer> elementos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_palabras);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Para el Menu
        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];

        gridView_palabras = findViewById(R.id.gridView_palabras);

        texto_palabras = getIntent().getStringExtra(Variables.FRASES);
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);


        fillGrid(true);

    }


    /**
     * Dado el texto completo. Llama al servicio Spacy para buscar el lema de las palabras
     * @param ini
     */
    private void fillGrid(boolean ini) {

        if (ini) {
            try {
                ConexionSpacy conexionSpacy = new ConexionSpacy(this, texto_palabras, "texto", "morfologico");
                conexionSpacy.start();
                conexionSpacy.join();
                palabras = conexionSpacy.getTextoMorfologico();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Crea los grids para los palabras
        GridViewPalabraAdapter gridViewAdapter = new GridViewPalabraAdapter(palabras, ini);
        gridView_palabras.setAdapter(gridViewAdapter);

    }


    private class GridViewPalabraAdapter extends BaseAdapter {

        // Array list con todas los palabras
        private ArrayList<ArrayList<String>> p;
        // private String[] palabras;
        private LayoutInflater layoutInflater;
        // Un array que llevara la cuenta del pictograma que esta saliendo por pantalla
        private boolean[] palabra_seleccionada;

        private boolean ini;


        private GridViewPalabraAdapter(ArrayList<ArrayList<String>> palabras, boolean ini) {
            super();
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            p = palabras;
            this.palabra_seleccionada = new boolean[p.size()];
            for (int i = 0; i < palabra_seleccionada.length; i++) {
                palabra_seleccionada[i] = false;
            }
            this.ini = ini;
        }


        @Override
        public int getCount() {
            return p.size();
        }

        @Override
        public Object getItem(int position) {
            return palabras.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = layoutInflater.inflate(R.layout.palabras, null);
            final ViewHolderGridPalabra holder = new ViewHolderGridPalabra();

            // PALABRA !!!!!!!!
            holder.textView = convertView.findViewById(R.id.textView_muestra_palabras);
            // Poner nombre de picto

            holder.textView.setText(p.get(position).get(1));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pasarAPalabrasAccion(position);
                }
            });

            if (!ini){
                if (mayus) {
                    holder.textView.setText(p.get(position).get(1).toUpperCase());
                }
                else {
                    holder.textView.setText(p.get(position).get(1).toLowerCase());
                }
            }

            convertView.setTag(holder);

            return convertView;
        }
    }

    public static class ViewHolderGridPalabra {
        TextView textView;
    }


    private void pasarAPalabrasAccion(int i) {
        Intent intent = new Intent(this, PalabrasAccionActivity.class);
        intent.putExtra(Variables.FRASES, palabras.get(i));
        intent.putExtra(Variables.MAYUS, mayus);
        startActivity(intent);
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
            texto_palabras = texto_palabras.toLowerCase();
        }
        else {
            mayus = true;
            texto_palabras = texto_palabras.toUpperCase();
        }
        fillGrid(false);
        // textView_original.setText(texto_original);
    }

    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(PalabrasActivity.this);
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
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(PalabrasActivity.this);
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
