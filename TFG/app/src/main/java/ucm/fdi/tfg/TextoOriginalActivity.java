package ucm.fdi.tfg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.conexionServidor.ConexionGrafeno;
import ucm.fdi.tfg.frases.FrasesActivity;
import ucm.fdi.tfg.ocr.OcrCaptureActivity;
import ucm.fdi.tfg.palabras.PalabrasActivity;

public class TextoOriginalActivity extends AppCompatActivity {

    // Donde se mostrará el texto capturado por pantalla
    TextView textView_original;
    ImageView imageView_audio;
    // Lectura en voz alta
    TextToSpeech tts;

    // Donde se guarda el texto capturado
    private String texto_original = "";
    // Donde se guarda el resumen del texto capturado.
    private String texto_resumen = "";
    // Para saber si esta en mayusculas o minusculas.
    private boolean mayus = false;


    // Desde esta clase llamamos a Grafeno porque tarda en hacer el resumen.
    // Por tanto lanzamos un hilo en segundo plano para que vaya trabajando.
    private ConexionGrafeno conexionGrafeno;
    // Comprobamos si el resumen ya esta cargado.
    private boolean resumenCargado = false;
    // Ventana que se muestra mientas el resumen del texto se carga.
    ProgressDialog progressDoalog;


    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;
    private ArrayList<Integer> elementos = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_original);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Para el Menu
        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];


        // Botones que se muestan en la activity
        Button button_resumen  = findViewById(R.id.button_resumen_original);
        Button button_frases   = findViewById(R.id.button_frases_original);
        Button button_palabras = findViewById(R.id.button_palabras_original);


        // Donde se muestra el texto capurado
        textView_original = findViewById(R.id.textView_resultado_original);
        // Guardamos el texto capturado en una variable.
        final String text = getIntent().getStringExtra(OcrCaptureActivity.TextBlockObject);
        // Reemplazamos los saltos del lineas por espacios.
        texto_original = text.replaceAll("\n", " ");

        // Buscamos el resumen del texto en un segundo plano
        conexionGrafeno = new ConexionGrafeno(this, texto_original);
        // Lanzamos grafeno para hacer el resumen del texto
        // Esto se hace asi porque el servicio tarda mucho en cargar,
        // por tanto, se accede en esa clase como un segundo plano. Asi, cuando se
        // seleccione resumen no hay tantos problemas de espera.
        conexionGrafeno.start();

        // Mostramos el texto original por pantalla
        textView_original.setText(texto_original);

        imageView_audio = findViewById(R.id.imageView_audio_original);

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
                    tts.speak(texto_original, TextToSpeech.QUEUE_FLUSH, null);
                    imageView_audio.setImageResource(R.drawable.no_audio);
                }
            }
        });



        // ******** PASAR A RESUMEN ********
        button_resumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el resumen ya ha sido cargado
                if (resumenCargado) {
                    // Pasamos directamente al siguiente activity
                    pasarASiguienteActivity("resumen");
                } else {
                    // Si no
                    // Creamos un Dialog mientras se carga.
                    progressDoalog = new ProgressDialog(TextoOriginalActivity.this);
                    progressDoalog.setMessage("PROCESANDO TEXTO....");
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    progressDoalog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (!resumenCargado) {
                                    Thread.sleep(200);
                                    conexionGrafeno.join();
                                    // Cuando se cargue
                                    if (!conexionGrafeno.getResumen().equals("")) {
                                        progressDoalog.dismiss();
                                        resumenCargado = true;
                                        texto_resumen = conexionGrafeno.getResumen();
                                        pasarASiguienteActivity("resumen");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
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
     * Dada una opcion, creamos el siguiente acvivity y pasamos los datos.
     * @param opcion resumen, frases, palabras
     */
    private void pasarASiguienteActivity(String opcion) {

        if (tts.isSpeaking()) {
            tts.stop();
            imageView_audio.setImageResource(R.drawable.audio);
        }

        Intent intent;
        // Intent para pasar al siguiente Activity
        switch (opcion) {
            case "frases":
                intent = new Intent(this, FrasesActivity.class);
                // Pasamos los datos:
                // 1. El texto completo
                // 2. Si esta en mayusculas o minusculas.
                intent.putExtra(Variables.FRASES, texto_original);
                intent.putExtra(Variables.MAYUS, mayus);
                // Lanzar activity
                startActivity(intent);
                break;
            case "palabras":
                intent = new Intent(this, PalabrasActivity.class);
                // Pasamos los datos:
                // 1. El texto completo
                // 2. Si esta en mayusculas o minusculas.
                intent.putExtra(Variables.FRASES, texto_original);
                intent.putExtra(Variables.MAYUS, mayus);
                // Lanzar activity
                startActivity(intent);
                break;
            case "resumen":
                intent = new Intent(this, TextoResumenActivity.class);
                if (mayus) {
                    texto_resumen = texto_resumen.toUpperCase();
                } else {
                    texto_resumen = texto_resumen.toLowerCase();
                }
                intent.putExtra(Variables.FRASES, texto_resumen);
                intent.putExtra(Variables.MAYUS, mayus);
                // Lanzar activity
                startActivity(intent);
                break;
        }
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
            texto_original = texto_original.toLowerCase();
        }
        else {
            mayus = true;
            texto_original = texto_original.toUpperCase();
        }
        textView_original.setText(texto_original);
    }

    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(TextoOriginalActivity.this);
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
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(TextoOriginalActivity.this);
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
