package ucm.fdi.tfg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ucm.fdi.tfg.ocr.OcrCaptureActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {


    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;

    // Para el menú
    private String fichero;
    private String[] elementos_menu;
    private boolean[] elementos_seleccionados;
    private ArrayList<Integer> elementos = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        autoFocus = findViewById(R.id.auto_focus);
        useFlash = findViewById(R.id.use_flash);

        findViewById(R.id.imageView_camara).setOnClickListener(this);



        elementos_menu = getResources().getStringArray(R.array.array_menu);
        elementos_seleccionados = new boolean[elementos_menu.length];

    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageView_camara) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivity(intent);

            // startActivityForResult(intent, RC_OCR_CAPTURE);
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


        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
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


/*
 * private void fillList() {
 *         checkTextAdapter = new CheckTextAdapter();
 *
 *         for(String f : qqq.split(" ")) {
 *             checkTextAdapter.addFrase(f);
 *         }
 *         checkTextAdapter.iniSeleccionadas();
 *
 *         lista.setAdapter(checkTextAdapter);
 *     }
 *
 *
 *     private class CheckTextAdapter extends BaseAdapter {
 *
 *         private ArrayList<String> palabras = new ArrayList<String>();
 *         private LayoutInflater layoutInflater;
 *         private boolean[] p_seleccionada;
 *
 *         public CheckTextAdapter() {
 *             super();
 *             layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 *         }
 *
 *         public void addFrase(String palabra){
 *             palabras.add(palabra);
 *             notifyDataSetChanged();
 *         }
 *
 *         public void iniSeleccionadas() {
 *             p_seleccionada = new boolean[palabras.size()];
 *         }
 *
 *
 *         public String getPalabrasSeleccionadas() {
 *
 *             String palabras_seleccionadas = "";
 *
 *             for (int i = 0; i < p_seleccionada.length; i++) {
 *                 if (p_seleccionada[i]) {
 *                     palabras_seleccionadas += palabras.get(i);
 *                 }
 *             }
 *
 *             return palabras_seleccionadas;
 *         }
 *
 *         @Override
 *         public int getCount() {
 *             return palabras.size();
 *         }
 *
 *         @Override
 *         public Object getItem(int position) {
 *             return palabras.get(position).toString();
 *         }
 *
 *         @Override
 *         public long getItemId(int position) {
 *             return position;
 *         }
 *
 *         @Override
 *         public View getView(final int position, View convertView, ViewGroup parent) {
 *             convertView = layoutInflater.inflate(R.layout.fila_frases, null);
 *             final ViewHolder holder = new ViewHolder();
 *             holder.txtItem = convertView.findViewById(R.id.text_palabras);
 *             holder.txtItem.setOnClickListener(new View.OnClickListener() {
 *                 @Override
 *                 public void onClick(View v) {
 *                     holder.txtItem.setText("JEJEJEJE");
 *                 }
 *             });
 *
 *             convertView.setTag(holder);
 *             return convertView;
 *         }
 *
 *     }
 *
 *
 *     public static class ViewHolder {
 *         public TextView txtItem;
 *     }
 */

/*
 * Called when an activity you launched exits, giving you the requestCode
 * you started it with, the resultCode it returned, and any additional
 * data from it.  The <var>resultCode</var> will be
 * {@link #RESULT_CANCELED} if the activity explicitly returned that,
 * didn't return any result, or crashed during its operation.
 * <p/>
 * <p>You will receive this call immediately before onResume() when your
 * activity is re-starting.
 * <p/>
 *
 * requestCode The integer request code originally supplied to
 *                    startActivityForResult(), allowing you to identify who this
 *                    result came from.
 * resultCode  The integer result code returned by the child activity
 *                    through its setResult().
 * data        An Intent, which can return result data to the caller
 *                    (various data can be attached to Intent "extras").
 * @see #startActivityForResult
 * @see #createPendingResult
 * @see #setResult(int)
 */
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    System.out.println(text);

                    String t = text.replaceAll("\n", " ");

                    // Establecer la conexion con el servidor
                    ConexionAPI hilo_conexion = new ConexionAPI(t, "definicion");
                    hilo_conexion.start();

                    try {
                        hilo_conexion.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // System.out.println(hilo_con.text_result);

                    ArrayList<String> result = hilo_conexion.getResultado();
                    String re = "";


                    for (String r : result) {
                        re += "* " + r + "\n";
                    }

                    textValue.setText(re);
                    // Valor devuelto de la conexion

                    Log.d(TAG, "Text read: " + text);
                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/