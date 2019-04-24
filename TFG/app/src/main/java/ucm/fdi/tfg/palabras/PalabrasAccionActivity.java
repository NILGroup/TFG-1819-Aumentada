package ucm.fdi.tfg.palabras;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.conexionServidor.ConexionSESAT;
import ucm.fdi.tfg.conexionServidor.ConexionSpacy;

public class PalabrasAccionActivity extends AppCompatActivity {


    private String texto_palabra;
    private boolean mayus;

    private LinearLayout linearLayout_palabras;
    private TextView textView_resultado;


    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_palabras_accion);

        final String palabra = getIntent().getStringExtra(Variables.FRASES);

        TextView textView_palabra = findViewById(R.id.textView_palabras_accion);
        textView_palabra.setText("Palabra: " + palabra);

        this.linearLayout_palabras = findViewById(R.id.layout_palabras_accion_resultado);

        this.textView_resultado = new TextView(getApplicationContext());
        this.textView_resultado.setText("Selecciona una opción");
        this.textView_resultado.setTextSize(23);
        this.textView_resultado.setPaddingRelative(
                this.textView_resultado.getPaddingStart() + 28,
                this.textView_resultado.getPaddingTop() + 28,
                this.textView_resultado.getPaddingEnd() + 28,
                this.textView_resultado.getPaddingBottom() + 28);
        linearLayout_palabras.addView(this.textView_resultado);

        Button button_definicion = findViewById(R.id.button_definicion_palabras);
        Button button_pictograma = findViewById(R.id.button_pictograma_palabras);
        Button button_sinonimos  = findViewById(R.id.button_sinonimos_palabras);
        Button button_antonimos  = findViewById(R.id.button_antonimos_palabras);

        button_definicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaResultado("definicion", palabra);
            }
        });

        button_pictograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaResultado("dificultad", palabra);
            }
        });

        button_sinonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaResultado("sinonimos", palabra);
            }
        });

        button_antonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscaResultado("antonimos", palabra);
            }
        });

    }


    private void buscaResultado(String servicio, String palabra) {

        try {
            ConexionSpacy conexionSpacy = new ConexionSpacy(this, palabra);
            conexionSpacy.start();
            conexionSpacy.join();
            String infinitivo = conexionSpacy.getResultado();

            ConexionSESAT conexionSESAT = new ConexionSESAT(infinitivo, servicio);
            conexionSESAT.start();
            conexionSESAT.join();
            ArrayList<String> resultado = conexionSESAT.getResultado();

            StringBuilder re = new StringBuilder();
            for (String r : resultado) {
                re.append(" * ");
                re.append(r);
                re.append("\n");
            }

            textView_resultado.setText(re.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}