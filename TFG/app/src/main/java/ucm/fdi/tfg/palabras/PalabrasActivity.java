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

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.VARIABLES.Variables;

public class PalabrasActivity extends AppCompatActivity {

    private TextView textView_palabras;

    private String texto_palabras;
    private boolean mayus;

    private GridView gridView_palabras;
    private GridViewPalabraAdapter gridViewAdapter;

    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_palabras);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridView_palabras = (GridView) findViewById(R.id.gridView_palabras);

        texto_palabras = getIntent().getStringExtra(Variables.FRASES);
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);

        // Crea los grids para los pictos
        gridViewAdapter = new GridViewPalabraAdapter(texto_palabras.split(" "));
        gridView_palabras.setAdapter(gridViewAdapter);

    }



    private class GridViewPalabraAdapter extends BaseAdapter {

        // Array list con todos los pictogramas
        private String[] palabras;
        private LayoutInflater layoutInflater;
        // Un array que llevara la cuenta del pictograma que esta saliendo por pantalla
        private boolean[] palabra_seleccionada;


        private GridViewPalabraAdapter(String[] palabras) {
            super();
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.palabras = palabras;
            this.palabra_seleccionada = new boolean[this.palabras.length];
            for (int i = 0; i < palabra_seleccionada.length; i++) {
                palabra_seleccionada[i] = false;
            }
        }

        @Override
        public int getCount() {
            return palabras.length;
        }

        @Override
        public Object getItem(int position) {
            return palabras[position];
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
            holder.textView.setText(palabras[position]);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pasarAPalabrasAccion(palabras[position]);
                }
            });

            /*if (mayus) {
                holder.textView.setText(palabras[position].toUpperCase());
            }
            else {
                holder.textView.setText(palabras[position].toLowerCase());
            }*/

            convertView.setTag(holder);

            return convertView;
        }
    }

    public static class ViewHolderGridPalabra {
        TextView textView;
    }


    private void pasarAPalabrasAccion(String palabra) {
        Intent intent = new Intent(this, PalabrasAccionActivity.class);
        intent.putExtra(Variables.FRASES, palabra);
        startActivity(intent);
    }



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
        // textView_original.setText(texto_original);
    }

    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
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
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setTitle("SELECCIONA LAS OPCIONES");
        adBuilder.setMultiChoiceItems(elementos_menu, elementos_seleccionados, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // if(isChecked){

                // }
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
