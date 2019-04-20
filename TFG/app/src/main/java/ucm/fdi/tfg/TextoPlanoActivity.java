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
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import ucm.fdi.tfg.frases.FrasesActivity;
import ucm.fdi.tfg.ocr.OcrCaptureActivity;

public class TextoPlanoActivity extends AppCompatActivity {

    public final static String FRASES = "FRASES";
    public final static String MAYUS = "MAYUS";

    private TextView text_result;
    private TextToSpeech tts;

    private ImageView imageView_audio;
    private ImageView imageView_mayusA;

    private Button button_resumen;
    private Button button_palabras;
    private Button button_frases;

    private String t = "";
    private boolean mayus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_plano);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView_audio  = findViewById(R.id.imageView_audio);
        imageView_mayusA = findViewById(R.id.imageView_mayusA);

        button_resumen  = findViewById(R.id.button_resumen);
        button_frases   = findViewById(R.id.button_frases);
        button_palabras = findViewById(R.id.button_palabras);

        text_result = findViewById(R.id.text_result);

        final String text = getIntent().getStringExtra(OcrCaptureActivity.TextBlockObject);

        t = text.replaceAll("\n", " ");
        text_result.setText(t);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    tts.setLanguage(new Locale("spa","ESP"));
            }
        });


        // ******** TEXT TO SPEECH ********
        imageView_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speak = text_result.getText().toString();
                Toast.makeText(getApplicationContext(), speak, Toast.LENGTH_SHORT).show();
                tts.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });


        // ******** MAYUSCULAS O MINUSCULAS ********
        imageView_mayusA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia mayusculas o minusculas segun pulsemos el boton
                if (mayus) {
                    mayus = false;
                    t = t.toLowerCase();
                }
                else {
                    mayus = true;
                    t = t.toUpperCase();
                }
                text_result.setText(t);
            }
        });


        // ******** PASAR A RESUMEN ********
        button_resumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarASiguienteActivity("resumen");
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
            case "resumen":
                intent = new Intent(this, TextoResumenActivity.class);
                break;
        }


        // Pasamos los datos:
        // 1. El texto completo
        // 2. Si esta en mayusculas o minusculas.
        intent.putExtra(TextoPlanoActivity.FRASES, t);
        intent.putExtra(TextoPlanoActivity.MAYUS, mayus);
        // Lanzar activity
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
