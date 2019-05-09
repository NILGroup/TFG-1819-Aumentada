package ucm.fdi.tfg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.conexionServidor.ConexionGrafeno;
import ucm.fdi.tfg.frases.FrasesActivity;
import ucm.fdi.tfg.palabras.PalabrasActivity;


public class TextoResumenActivity extends AppCompatActivity {

    // Donde se muestra el resumen del texto
    private TextView textView_resumen;
    private ImageView imageView_audio;
    // Lectura en voz alta
    private TextToSpeech tts;

    // Donde se guarda el resumen del texto
    private String texto_resumen = "";
    // Para cambiar de mayusculas a minusculas o viceversa
    private boolean mayus = false;


    // Para el menú
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


        // Botones que aparecen el la pantalla
        imageView_audio = findViewById(R.id.imageView_audio_resumen);
        Button button_original = findViewById(R.id.button_original_resumen);
        Button button_frases   = findViewById(R.id.button_frases_resumen);
        Button button_palabras = findViewById(R.id.button_palabras_resumen);


        // TextView donde aparece el texto.
        textView_resumen = findViewById(R.id.textView_resultado_resumen);


        // Datos del intent anterior.
        texto_resumen = getIntent().getStringExtra(Variables.FRASES);
        mayus = getIntent().getBooleanExtra(Variables.MAYUS, false);

        // Se muestra el texto resumido.
        textView_resumen.setText(texto_resumen);



        // Text to speech
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    tts.setLanguage(new Locale("spa","ESP"));
            }
        });

        if (tts.isSpeaking()) {
            imageView_audio.setImageResource(R.drawable.no_audio);
        } else {
            imageView_audio.setImageResource(R.drawable.audio);
        }


        // ******** TEXT TO SPEECH ********
        imageView_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts.isSpeaking()) {
                    tts.stop();
                    imageView_audio.setImageResource(R.drawable.audio);
                } else {
                    Toast.makeText(getApplicationContext(), "Sonando audio", Toast.LENGTH_SHORT).show();
                    tts.speak(texto_resumen, TextToSpeech.QUEUE_FLUSH, null);
                    imageView_audio.setImageResource(R.drawable.no_audio);
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


    /**
     * Dada una opción, pasa al siguiente activity pasandole los parametros
     * @param opcion frases, palabras
     */
    private void pasarASiguienteActivity(String opcion) {

        if (tts.isSpeaking()) {
            tts.stop();
            imageView_audio.setImageResource(R.drawable.audio);
        }

        Intent intent = new Intent();
        String aux_resumen = texto_resumen.replace("\n", " ");

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
        intent.putExtra(Variables.FRASES, aux_resumen);
        intent.putExtra(Variables.MAYUS, mayus);
        // Lanzar activity
        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        if (tts.isSpeaking()) {
            tts.stop();
        }
        //ejecuta super.onBackPressed() para que finalice el metodo cerrando el activity
        super.onBackPressed();
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
            texto_resumen = texto_resumen.toLowerCase();
        }
        else {
            mayus = true;
            texto_resumen = texto_resumen.toUpperCase();
        }
        textView_resumen.setText(texto_resumen);
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