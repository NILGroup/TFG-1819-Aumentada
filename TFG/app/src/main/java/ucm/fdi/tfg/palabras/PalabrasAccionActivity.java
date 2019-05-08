package ucm.fdi.tfg.palabras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    private TextView textView_palabra;
    private TextView textView_resultado;
    private ImageView imageView_resultado;

    private ArrayList<String> palabra;
    private boolean mayus;
    private String urlPath;

    ArrayList<ArrayList<String>> res;
    private int pictos;


    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;
    private ArrayList<Integer> elementos = new ArrayList<>();



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_palabras_accion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Para el Menu
        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];


        this.urlPath = hallarUrl(this);


        // Desde Palabras Activity se le pasa un arrayList de 3 elementos
        //      0 - lema
        //      1 - palabra
        //      2 - parte
        palabra = getIntent().getStringArrayListExtra(Variables.FRASES);
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);


        // TextView para saber con que palabra se está trabajando.
        textView_palabra = findViewById(R.id.textView_palabras_accion);
        textView_palabra.setText("Palabra: " + palabra.get(1));


        // Para mostrar el resultado de los sevicios.
        this.linearLayout_palabras = findViewById(R.id.layout_palabras_accion_resultado);

        // Para mostrar el picto
        this.imageView_resultado = new ImageView(getApplicationContext());

        // Para mostrar el resto de los servicios
        this.textView_resultado = new TextView(getApplicationContext());
        this.textView_resultado.setText("Selecciona una opción");
        this.textView_resultado.setTextSize(23);
        this.textView_resultado.setPaddingRelative(
                this.textView_resultado.getPaddingStart() + 26,
                this.textView_resultado.getPaddingTop(),
                this.textView_resultado.getPaddingEnd() + 26,
                this.textView_resultado.getPaddingBottom());
        textView_resultado.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout_palabras.addView(this.textView_resultado);


        // Botones de toda la pantalla
        Button button_definicion = findViewById(R.id.button_definicion_palabras);
        Button button_pictograma = findViewById(R.id.button_pictograma_palabras);
        Button button_sinonimos  = findViewById(R.id.button_sinonimos_palabras);
        Button button_antonimos  = findViewById(R.id.button_antonimos_palabras);



        button_definicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaResultado("definicion");
            }
        });

        button_pictograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaPicto();
            }
        });

        button_sinonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaResultado("sinonimos");
            }
        });

        button_antonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaResultado("antonimos");
            }
        });

    }



    /**
     * Busca el fichero con la url correspondiente.
     * @param c context
     * @return devuelve la url.
     */
    private String hallarUrl(Context c) {
        String url = "";
        try {
            InputStream fraw = c.getResources().openRawResource(R.raw.pictar2);
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
            url = brin.readLine();
            fraw.close();
        }
        catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }

        return url;
    }





    private void buscaPicto() {

        // Establecer la conexion con el servidor pictar1
        ConexionPICTAR conexionPICTAR = new ConexionPICTAR(palabra.get(1), this);
        conexionPICTAR.start();

        try {
            conexionPICTAR.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        res = conexionPICTAR.getResultado();
        pictos = 1;

        // si pulsamos en la imagen
        imageView_resultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AQUI CAMBIAMOS EL PICTO!!!!!!
                // El array siempre tendra 2 elementos. Por tanto si hay 3 o mas elementos
                // significa que tendrá mas de 1 pictograma
                if (res.get(0).size() > 2) {
                    if (pictos < res.get(0).size() - 1) {
                        pictos += 1;
                    }
                    else {
                        pictos = 1;
                    }
                    Picasso.get()
                            .load(urlPath +
                                    res.get(0).get(pictos))
                            .into(imageView_resultado);
                }

            }
        });

        // Poner imagen
        if (res.get(0).get(pictos).equals(Variables.PICTOS_NOT_FOUND)) {
            // Si la palabra no tiene pictogramas. Pone una imagen -> una x roja
            Bitmap bmp;
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pictos_not_found);
            bmp = Bitmap.createScaledBitmap(bmp, 500, 500, true);
            imageView_resultado.setImageBitmap(bmp);
            Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                            "Esta palabra no tiene pictograma", Toast.LENGTH_SHORT);

            toast1.show();
            // imageView_resultado.setImageResource(R.drawable.pictos_not_found);
        } else {
            // Si la palabra o frase tiene pictograma,
            // selecciona el primer pictograma del array
            Picasso.get().load(urlPath + res.get(0).get(pictos)).into(imageView_resultado);
        }

        linearLayout_palabras.removeView(this.textView_resultado);
        linearLayout_palabras.addView(this.imageView_resultado);
    }


    private void buscaResultado(String servicio) {

        try {

            ConexionAPI conexionAPI = new ConexionAPI(this, this.palabra.get(0), servicio);
            conexionAPI.start();
            conexionAPI.join();

            if (conexionAPI.tieneResultado()) {
                ArrayList<String> resultado = conexionAPI.getResultado();

                StringBuilder re = new StringBuilder();
                for (String r : resultado) {
                    re.append("*  ");
                    re.append(r);
                    re.append("\n");
                }

                if (mayus) {
                    textView_resultado.setText(re.toString().toUpperCase());
                }else {
                    textView_resultado.setText(re.toString().toLowerCase());
                }


                linearLayout_palabras.removeAllViews();
                linearLayout_palabras.addView(this.textView_resultado);
            }
            else {
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "No se ha podido encontrar el resultado", Toast.LENGTH_SHORT);

                toast1.show();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        }
        else {
            mayus = true;
            textView_palabra.setText("Palabra: " + palabra.get(1).toUpperCase());
        }


    }

    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(PalabrasAccionActivity.this);
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
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(PalabrasAccionActivity.this);
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