package ucm.fdi.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ucm.fdi.tfg.frases.FrasesActivity;

public class TextoResumenActivity extends AppCompatActivity {

    public final static String FRASES = "FRASES";
    public final static String MAYUS = "MAYUS";

    private TextView text_result;

    private ImageView imageView_audio;
    private ImageView imageView_mayusA;

    private Button button_original;
    private Button button_palabras;
    private Button button_frases;

    private String text = "";
    private boolean mayus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_resumen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView_audio  = findViewById(R.id.imageView_Raudio);
        imageView_mayusA = findViewById(R.id.imageView_RmayusA);

        button_original = findViewById(R.id.button_Roriginal);
        button_frases   = findViewById(R.id.button_Rfrases);
        button_palabras = findViewById(R.id.button_Rpalabras);

        text_result = findViewById(R.id.text_result_resumen);

        // Lo que nos manda el activity anterior
        text = getIntent().getStringExtra(TextoPlanoActivity.FRASES);
        mayus = getIntent().getBooleanExtra(TextoPlanoActivity.MAYUS, false);


        /**
         * ANTES DE MOSTRAR EL TEXTO POR PANTALLA TENDREMOS
         * QUE PONER EL METODO DE HACER RESUMEN. GRAFENO
         */
        // Mostrarlo por pantalla
        text_result.setText(text);


        // ******** TEXT TO SPEECH ********
        imageView_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui añadir la funcionalidad del speech
            }
        });


        // ******** MAYUSCULAS O MINUSCULAS ********
        imageView_mayusA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia mayusculas o minusculas segun pulsemos el boton
                if (mayus) {
                    mayus = false;
                    text = text.toLowerCase();
                }
                else {
                    mayus = true;
                    text = text.toUpperCase();
                }
                text_result.setText(text);
            }
        });


        // ******** PASAR A TEXTO ORIGINAL ********
        button_original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ******** PASAR A PALABRAS ********


        // ******** PASAR A FRASES ********
        button_frases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarASiguienteActivity("frases");
            }
        });

    }

    private void pasarASiguienteActivity(String opcion) {

        Intent intent = new Intent();

        // Intent para pasar al siguiente Activity
        switch (opcion) {
            case "frases":
                intent = new Intent(this, FrasesActivity.class);
                break;
            case "palabras":
                break;
        }

        if (intent != null) {
            // Pasamos los datos:
            // 1. El texto completo
            // 2. Si esta en mayusculas o minusculas.
            intent.putExtra(TextoPlanoActivity.FRASES, text);
            intent.putExtra(TextoPlanoActivity.MAYUS, mayus);
            // Lanzar activity
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.MnuOpc1) {
        //   return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
