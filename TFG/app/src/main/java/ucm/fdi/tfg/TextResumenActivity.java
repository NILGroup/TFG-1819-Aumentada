package ucm.fdi.tfg;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ucm.fdi.tfg.conexionServidor.ConexionAPI;


public class TextResumenActivity extends AppCompatActivity {

    private static final String TAG = "TextResumenActivity";
    // private TextView statusMessage;
    private TextView textValue;
    private TextView textResult;
    private TextView textSeleccinado;

    private Button button_sinonimo;
    private Button button_antonimo;
    private Button button_definicion;
    private Button button_spacy;

    private Context context;

    private String t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_resumen);

        // statusMessage = (TextView)findViewById(R.id.status_message);
        textValue = (TextView)findViewById(R.id.text_value);
        textResult = (TextView)findViewById(R.id.text_result);
        textSeleccinado = (TextView) findViewById(R.id.text_opcion_seleccionada);

        button_sinonimo = (Button) findViewById(R.id.button_sinonimo);
        button_antonimo = (Button) findViewById(R.id.button_antonimo);
        button_definicion = (Button) findViewById(R.id.button_definicion);
        button_spacy = (Button) findViewById (R.id.button_spacy);

        context = this;

        final String text = getIntent().getStringExtra(OcrCaptureActivity.TextBlockObject);
        // statusMessage.setText(R.string.ocr_success);
        System.out.println(text);

        t = text.replaceAll("\n", " ");

        textValue.setText(t);


        button_sinonimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarServidor("sinonimos");
            }
        });

        button_antonimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarServidor("antonimos");
            }
        });

        button_definicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarServidor("definicion");
            }
        });

        button_spacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarS("morfologico");
            }
        });

        Log.d(TAG, "Text read: " + text);
    }

    protected void conectarS(String servicio) {

        // Establecer la conexion con el servidor
        ConexionSpacy hilo_conexion = new ConexionSpacy(t, servicio, context);
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

        textResult.setText(re);
        textSeleccinado.setText(servicio);
        // Valor devuelto de la conexion
    }

    protected void conectarServidor(String servicio) {

        // Establecer la conexion con el servidor
        ConexionAPI hilo_conexion = new ConexionAPI(t, servicio);
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

        textResult.setText(re);
        textSeleccinado.setText(servicio);
        // Valor devuelto de la conexion
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}
