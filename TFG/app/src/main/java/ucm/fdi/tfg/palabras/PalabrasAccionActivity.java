package ucm.fdi.tfg.palabras;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView textView_resultado;
    private ImageView imageView_resultado;

    private ArrayList<String> palabra;
    private String urlPath;


    ArrayList<ArrayList<String>> res;
    private int pictos;

    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_palabras_accion);

        this.urlPath = hallarUrl(this);


        // Desde Palabras Activity se le pasa un arrayList de 3 elementos
        //      0 - lema
        //      1 - palabra
        //      2 - parte
        palabra = getIntent().getStringArrayListExtra(Variables.FRASES);

        // TextView para saber con que palabra se está trabajando.
        TextView textView_palabra = findViewById(R.id.textView_palabras_accion);
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
        ConexionPICTAR conexionPICTAR = new ConexionPICTAR(palabra.get(0), this);
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
            imageView_resultado.setImageResource(R.drawable.pictos_not_found);
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
            ArrayList<String> resultado = conexionAPI.getResultado();

            StringBuilder re = new StringBuilder();
            for (String r : resultado) {
                re.append(" *  ");
                re.append(r);
                re.append("\n");
            }

            textView_resultado.setText(re.toString());

            linearLayout_palabras.removeAllViews();
            linearLayout_palabras.addView(this.textView_resultado);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}