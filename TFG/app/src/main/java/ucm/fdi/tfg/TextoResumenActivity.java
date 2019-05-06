package ucm.fdi.tfg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.frases.FrasesActivity;
import ucm.fdi.tfg.palabras.PalabrasActivity;

public class TextoResumenActivity extends AppCompatActivity {

    TextView textView_resumen;
    TextToSpeech tts;

    private String texto_resumen = "";
    private boolean mayus = false;

    // Para el menú
    private String fichero;
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;
    private ArrayList<Integer> elementos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_resumen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Para el Menu
        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];


        // Botones
        ImageView imageView_audio = findViewById(R.id.imageView_audio_resumen);

        Button button_original = findViewById(R.id.button_original_resumen);
        Button button_frases   = findViewById(R.id.button_frases_resumen);
        Button button_palabras = findViewById(R.id.button_palabras_resumen);

        // Texto capurado
        textView_resumen = findViewById(R.id.textView_resultado_resumen);

        texto_resumen = getIntent().getStringExtra(Variables.FRASES);;
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);

        textView_resumen.setText(texto_resumen);


        // Text to speech
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
                if (tts.isSpeaking()) {
                    tts.stop();
                } else {
                    Toast.makeText(getApplicationContext(), "Sonando audio", Toast.LENGTH_SHORT).show();
                    tts.speak(texto_resumen, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        // ******** PASAR A TEXTO ORIGINAL ********
        button_original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts.isSpeaking()) {
                    tts.stop();
                }
                finish();
            }
        });

        // ******** PASAR A PALABRAS ********
        button_palabras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarASiguienteActivity("palabras");
            }
        });

        // ******** PASAR A FRASES ********
        button_frases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarASiguienteActivity("frases");
            }
        });

    }

    private void pasarASiguienteActivity(String opcion) {

        if (tts.isSpeaking()) {
            tts.stop();
        }

        Intent intent = new Intent();

        // Intent para pasar al siguiente Activity
        switch (opcion) {
            case "frases":
                intent = new Intent(this, FrasesActivity.class);
                break;
            case "palabras":
                intent = new Intent(this, PalabrasActivity.class);
                break;
        }

        // Pasamos los datos:
        // 1. El texto completo
        // 2. Si esta en mayusculas o minusculas.
        intent.putExtra(Variables.FRASES, texto_resumen);
        intent.putExtra(Variables.MAYUS, mayus);
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
        switch (item.getItemId()) {
            case R.id.item_ajustes:
                pulsarBotonAjustes();
                break;
            case R.id.item_about_us:
                pulsarBotonAboutUs();
                break;
            case R.id.item_mayus:
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Captura un texto", Toast.LENGTH_SHORT);

                toast1.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(TextoResumenActivity.this);
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

        try {
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput("servicios.txt")));
            for (int i = 0; i < elementos_seleccionados.length; i++) {
                if (fin.readLine().equals("1")){
                    elementos_seleccionados[i] = true;
                }
                else {
                    elementos_seleccionados[i] = false;
                }
            }
            fin.close();
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }


        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(TextoResumenActivity.this);
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
                fichero = "";
                for (int i = 0; i < elementos_seleccionados.length; i++) {
                    if (elementos_seleccionados[i]) {
                        fichero += "1\n";
                    }
                    else {
                        fichero += "0\n";
                    }
                }
                try {
                    OutputStreamWriter fout= new OutputStreamWriter(openFileOutput("servicios.txt", Context.MODE_PRIVATE));
                    fout.write(fichero);
                    fout.close();
                } catch (Exception ex) {
                    Log.e("Ficheros", "Error al escribir fichero a memoria interna");
                }
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
                try {
                    OutputStreamWriter fout= new OutputStreamWriter(openFileOutput("servicios.txt", Context.MODE_PRIVATE));
                    fout.write("1\n1\n1\n1\n");
                    fout.close();
                } catch (Exception ex) {
                    Log.e("Ficheros", "Error al escribir fichero a memoria interna");
                }
            }
        });

        AlertDialog ad = adBuilder.create();
        ad.show();
    }
}