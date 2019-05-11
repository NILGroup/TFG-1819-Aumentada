package ucm.fdi.tfg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ucm.fdi.tfg.VARIABLES.Variables;
import ucm.fdi.tfg.ocr.OcrCaptureActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {


    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;


    // Para el menú
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;

    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // PARA EL MENU
        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];


        autoFocus = findViewById(R.id.auto_focus);
        useFlash = findViewById(R.id.use_flash);

        findViewById(R.id.imageView_camara).setOnClickListener(this);

        preferences = getSharedPreferences("servicios", Context.MODE_PRIVATE);
        if (!preferences.contains("servicio")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("servicio", "");
            editor.putBoolean("definicion", true);
            editor.putBoolean("sinonimos", true);
            editor.putBoolean("antonimos", true);
            editor.putBoolean("pictograma", true);
            editor.apply();
        }

    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageView_camara) {
            // Lanzamos Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivity(intent);
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
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Captura un texto", Toast.LENGTH_SHORT);

                    toast1.show();
                    break;
            }

        return super.onOptionsItemSelected(item);
    }



    private void pulsarBotonAboutUs() {
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
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

        elementos_seleccionados[0] = preferences.getBoolean("definicion", true);
        elementos_seleccionados[1] = preferences.getBoolean("sinonimos", true);
        elementos_seleccionados[2] = preferences.getBoolean("antonimos", true);
        elementos_seleccionados[3] = preferences.getBoolean("pictograma", true);


        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
        adBuilder.setTitle("SELECCIONA LAS OPCIONES");
        adBuilder.setMultiChoiceItems(elementos_menu, elementos_seleccionados, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                elementos_seleccionados[which] = isChecked;
            }
        });
        adBuilder.setCancelable(true);
        adBuilder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = preferences.edit();
                for (int i = 0; i < elementos_seleccionados.length; i++) {
                    editor.putBoolean(Variables.MENU[i], elementos_seleccionados[i]);
                }
                editor.apply();

                dialog.dismiss();
            }
        });

        AlertDialog ad = adBuilder.create();
        ad.show();
    }

}