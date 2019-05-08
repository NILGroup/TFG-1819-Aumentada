package ucm.fdi.tfg.conexionServidor;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ucm.fdi.tfg.R;
import ucm.fdi.tfg.VARIABLES.Variables;

public class ConexionPICTAR extends Thread {

    private String url = "";
    private ArrayList<ArrayList<String>> resultado = new ArrayList<ArrayList<String>>();
    private boolean conexionOK = false;


    public ConexionPICTAR(String cadena, Context c) {

        buscarUrl(c);

        if (!url.equals("")) {
            try {
                url += URLEncoder.encode(cadena, "UTF-8").replaceAll("\\+", "%20");
                conexionOK = true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    private void buscarUrl(Context c) {
        try {
            InputStream fraw = c.getResources().openRawResource(R.raw.pictar1);
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
            this.url = brin.readLine();
            fraw.close();
        }
        catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }
    }


    public ArrayList<ArrayList<String>> getResultado() {
        return this.resultado;
    }


    @Override
    public void run() {
        if (conexionOK) {
            URL obj;
            try {
                obj = new URL(url);

                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");

                // int responseCode = con.getResponseCode();

                if (con.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

                    String inputLine;

                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        // System.out.println(inputLine);
                        response.append(inputLine);
                    }

                    in.close();

                    con.disconnect();

                    // Procesa el resultado
                    procesaResultado(response.toString());
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Con el resultado devuelto por la conexion. Se procesa el Json
     * pictar1 no devuelve un JSON como tal.
     * Por tanto,
     *      1. separamos cada elementos en array
     *      2. quitamos las comillas
     *      3. si no hay pictos
     */
    private void procesaResultado(String resultado){

        if (!resultado.equals("[]")) {
            String[] array_info = resultado.substring(2, resultado.length() - 2).split("\",\"");

            for (int i = 0; i < array_info.length; i++) {
                String aux = array_info[i].replaceAll("\"", "");
                this.resultado.add(i, new ArrayList<String>());
                if (aux.length() > 15 && aux.substring(0, 15).equals(Variables.WORD_NOT_FOUND)) {
                    // Primer elemento: palabra
                    this.resultado.get(i).add(aux.substring(16));
                    // Primer elemento: palabra
                    this.resultado.get(i).add(Variables.PICTOS_NOT_FOUND);
                }
                else {
                    // Primer elemento: palabra
                    this.resultado.get(i).add(aux.substring(aux.indexOf("]") + 2, aux.length()));
                    // Segundo elemento: pictos
                    for (String s : aux.substring(aux.indexOf("[") + 1, aux.indexOf("]")).split(", ")){
                        this.resultado.get(i).add(s);
                    }
                }
            }
        }
    }

}
