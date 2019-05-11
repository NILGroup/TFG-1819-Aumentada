package ucm.fdi.tfg.palabras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.conexionServidor.ConexionAPI;
import ucm.fdi.tfg.conexionServidor.ConexionPICTAR;


public class PalabrasAccionActivity extends AppCompatActivity {

    private LinearLayout linearLayout_palabras;
    private LinearLayout linearLayout_resultado_servicios;
    private LinearLayout linearLayout_resultado_pictograma;

    private TextView textView_palabra;
    private TextView textView_resultado;
    private ImageView imageView_resultado;

    private StringBuilder re;

    private ArrayList<String> palabra;
    private boolean mayus;

    ArrayList<ArrayList<String>> res;
    private int pictos;

    private String urlPath;

    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_palabras_accion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Para el Menu
        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];

        // Para mostrar el resultado de los sevicios.
        this.linearLayout_palabras = findViewById(R.id.layout_palabras_accion_resultado);

        // Crear la parte de los botones
        // Se pasa true si es la primera vez que se crea
        crearBotonesServicios(true);


        // Buscar la segunda url de PICTAR para mostrar los u_pictos_frases
        buscarURL(this);



        // Desde Palabras Activity se le pasa un arrayList de 3 elementos
        //      0 - lema
        //      1 - palabra
        //      2 - parte
        palabra = getIntent().getStringArrayListExtra(Variables.FRASES);
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);


        // TextView para saber con que palabra se está trabajando.
        textView_palabra = findViewById(R.id.textView_palabras_accion);
        textView_palabra.setText("Palabra: " + palabra.get(1));


        muestraResultados();

        muestraPictos();

    }

    /**
     * Crea los u_boton en funcion de los servicios que se han seleccionado.
     */
    private void crearBotonesServicios(boolean ini) {

        // Actualiza los servicios seleccionados.
        actualizarServiciosSeleccionados();

        LinearLayout linearLayout_botones = findViewById(R.id.linearLayour_palabras_botones);

        if (!ini) {
            linearLayout_botones.removeAllViews();
        }

        for (int i = 0; i < elementos_seleccionados.length; i++) {
            if (elementos_seleccionados[i]) {
                // Creamos el layout con el boton
                LinearLayout linearLayout_boton = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.u_boton,null);
                Button boton = linearLayout_boton.findViewById(R.id.button_botones);

                // Ponemos nombre al boton
                boton.setText(elementos_menu[i]);
                // implementamos el evento
                boton.setOnClickListener(misEventosButton);

                // Añadimos los u_boton a la vista
                linearLayout_botones.addView(linearLayout_boton);
            }
        }

    }


    /**
     * Actualiza el array elementos_seleccionados
     */
    private void actualizarServiciosSeleccionados() {
        SharedPreferences preferences = getSharedPreferences("servicios", Context.MODE_PRIVATE);
        elementos_seleccionados[0] = preferences.getBoolean("definicion", true);
        elementos_seleccionados[1] = preferences.getBoolean("sinonimos", true);
        elementos_seleccionados[2] = preferences.getBoolean("antonimos", true);
        elementos_seleccionados[3] = preferences.getBoolean("pictograma", true);
    }

    private View.OnClickListener misEventosButton = new View.OnClickListener() {
        public void onClick(View v) {
            // Castemos la variable v (View) para que este se convierta en un boton
            Button objBoton = (Button) v;
            // Conectamos con el servicio correspondiente
            conectarConServicioAPI(objBoton.getText().toString());
        }
    };


    /**
     * Busca el fichero con la url pictar2.
     * @param c context
     */
    private void buscarURL(Context c) {
        try {
            InputStream fraw = c.getResources().openRawResource(R.raw.pictar2);
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
            this.urlPath = brin.readLine();
            fraw.close();
        }
        catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }
    }

    /**
     * Muestra los resultados del resto de servicios.
     */
    private void muestraResultados() {
        linearLayout_resultado_servicios = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.u_palabra_resultado,null);
        textView_resultado = linearLayout_resultado_servicios.findViewById(R.id.textView_palabra_resultado);

        textView_resultado.setText("Selecciona una opción");

        // linearLayout_palabras.removeAllViews();
        linearLayout_palabras.addView(linearLayout_resultado_servicios);
    }

    /**
     * Muestra los pictogramas.
     */
    private void muestraPictos() {
        linearLayout_resultado_pictograma = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.u_picto_palabra,null);
        // Para mostrar el picto
        this.imageView_resultado = linearLayout_resultado_pictograma.findViewById(R.id.imageView_picto_palabra);
    }


    /**
     * Dado el nombre del boton, llama a la funcion correspondiente.
     * @param nombre_boton nombre
     */
    public void conectarConServicioAPI(String nombre_boton) {

        if (nombre_boton.equals(elementos_menu[3])) {
            buscaPicto();
        } else {
            String aux = "";
            if (nombre_boton.equals(elementos_menu[0])) {
                aux = "definicion";
            } else if (nombre_boton.equals(elementos_menu[1])) {
                aux = "sinonimos";
            } else if (nombre_boton.equals(elementos_menu[2])) {
                aux = "antonimos";
            }
            buscaResultado(aux);
        }
    }


    /**
     * Llamamos al servicio de Spacy para buscar el pictograma asociado a la palabra.
     * Mostramos el pictograma.
     */
    private void buscaPicto() {

        try {
            // Establecer la conexion con el servidor pictar1
            ConexionPICTAR conexionPICTAR = new ConexionPICTAR(palabra.get(1), this);
            conexionPICTAR.start();
            conexionPICTAR.join();

            res = conexionPICTAR.getResultado();
            pictos = 1;

            // Si pulsamos en la imagen
            imageView_resultado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // AQUI CAMBIAMOS EL PICTO!!!!!!
                    // El array siempre tendra 2 elementos. Por tanto si hay 3 o mas elementos
                    // significa que tendrá mas de 1 pictograma
                    if (res.get(0).size() > 2) {
                        if (pictos < res.get(0).size() - 1) {
                            pictos += 1;
                        } else {
                            pictos = 1;
                        }
                        Picasso.get().load(urlPath + res.get(0).get(pictos)).into(imageView_resultado);
                    }
                }
            });

            // Poner imagen
            if (res.get(0).get(pictos).equals(Variables.PICTOS_NOT_FOUND)) {
                // Si la palabra no tiene pictogramas. Pone una imagen -> una x roja
                imageView_resultado.setImageResource(R.drawable.pictos_not_found);
                Toast toast1 = Toast.makeText(getApplicationContext(),
                                "Esta palabra no tiene pictograma", Toast.LENGTH_SHORT);
                toast1.show();
            } else {
                // Si la palabra o frase tiene pictograma,
                // selecciona el primer pictograma del array
                Picasso.get().load(urlPath + res.get(0).get(pictos)).into(imageView_resultado);
            }

            linearLayout_palabras.removeAllViews();
            linearLayout_palabras.addView(this.linearLayout_resultado_pictograma);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     * Dado un servicio, llamamos al servicio web de API accesibilidad.
     * Mostramos el resultado por pantalla
     * @param servicio definicion, sinonimos, anonimos.
     */
    private void buscaResultado(String servicio) {

        try {
            ConexionAPI conexionAPI = new ConexionAPI(this, this.palabra.get(0), servicio);
            conexionAPI.start();
            conexionAPI.join();

            if (conexionAPI.tieneResultado()) {
                ArrayList<String> resultado = conexionAPI.getResultado();

                re = new StringBuilder();
                for (String r : resultado) {
                    re.append("*  ");
                    re.append(r);
                    re.append("\n");
                }

                if (mayus) {
                    textView_resultado.setText(re.toString().toUpperCase());
                } else {
                    textView_resultado.setText(re.toString().toLowerCase());
                }

                linearLayout_palabras.removeAllViews();
                linearLayout_palabras.addView(this.linearLayout_resultado_servicios);
            }
            else {
                Toast toast1 = Toast.makeText(getApplicationContext(),
                                "No se ha podido encontrar el resultado", Toast.LENGTH_SHORT);
                toast1.show();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        //ejecuta super.onBackPressed() para que finalice el metodo cerrando el activity
        finish();
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
            textView_palabra.setText("Palabra: " + palabra.get(1).toLowerCase());
            if (re != null) {
                textView_resultado.setText(re.toString().toLowerCase());
            }
        }
        else {
            mayus = true;
            textView_palabra.setText("Palabra: " + palabra.get(1).toUpperCase());
            if (re != null) {
                textView_resultado.setText(re.toString().toUpperCase());
            }
        }
    }


    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(PalabrasAccionActivity.this);
        adBuilder.setTitle("LeeFácil");
        adBuilder.setMessage("Aplicación desarrollada por Elianni Agüero e Ignacio Sande como Trabajo de Fin de Grado.\n" +
                "Facultad de Informática.\n" +
                "Universidad Complutense de Madrid");
        adBuilder.setCancelable(true);
        adBuilder.setNegativeButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = adBuilder.create();
        ad.show();
    }



    private void pulsarBotonAjustes() {

        SharedPreferences preferences = getSharedPreferences("servicios", Context.MODE_PRIVATE);
        elementos_seleccionados[0] = preferences.getBoolean("definicion", true);
        elementos_seleccionados[1] = preferences.getBoolean("sinonimos", true);
        elementos_seleccionados[2] = preferences.getBoolean("antonimos", true);
        elementos_seleccionados[3] = preferences.getBoolean("pictograma", true);


        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(PalabrasAccionActivity.this);
        adBuilder.setTitle("SELECCIONA LAS OPCIONES");
        adBuilder.setMultiChoiceItems(elementos_menu, elementos_seleccionados, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                elementos_seleccionados[which] = isChecked;
                // cambiarConfiguracion(isChecked, which);
            }
        });
        adBuilder.setCancelable(true);
        adBuilder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = getSharedPreferences("servicios", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                for (int i = 0; i < elementos_seleccionados.length; i++) {
                    editor.putBoolean(Variables.MENU[i], elementos_seleccionados[i]);
                }
                editor.apply();

                dialog.dismiss();
                crearBotonesServicios(false);
            }
        });

        AlertDialog ad = adBuilder.create();
        ad.show();
    }

}