package ucm.fdi.tfg.conexionServidor;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ucm.fdi.tfg.R;


public class ConexionSpacy extends Thread {

    private Context c;

    private String servicio;
    private String cadena;
    private String analisis;
    private String urlPath;

    private String resultado;
    private String morfologico;
    private ArrayList<String> oraciones;
    private ArrayList<String> sinonimos;
    private ArrayList<ArrayList<String>> texto_morfologico;


    public ConexionSpacy(Context c, String cadena, String servicio, String analisis) {

        this.c = c;
        this.servicio = servicio;
        this.cadena = cadena;
        this.analisis = analisis;

        // Buscamos la url, en funcion del servicio
        hallarUrl(c);
    }

    /**
     * Devuelve el lema de la palabra
     * @return lema
     */
    public String getMorfologico() {
        procesaResultado("morfologico");
        return this.morfologico;
    }

    /**
     * Devuelve una lista de oraciones.
     * @return oraciones
     */
    public ArrayList<String> getOraciones() {
        procesaResultado("oraciones");
        return this.oraciones;
    }

    /**
     * Devuelve una lista de sinonimos
     * @return sinonimos
     */
    public ArrayList<String> getSinonimos() {
        procesaResultado("sinonimos");
        return this.sinonimos;
    }

    /**
     * Devuelve el analisis morfologico de todas las palabras dentro de un texto
     * @return morfologico
     */
    public ArrayList<ArrayList<String>> getTextoMorfologico() {
        procesaResultado("morfologico");
        return this.texto_morfologico;
    }


    /***
     * Dependiendo del servicio de Spacy que queramos utilizar, buscará su correspondiente URL
     * @param c Context
     */
    private void hallarUrl(Context c) {
        try {
            InputStream fraw;
            BufferedReader brin;
            switch (this.servicio) {
                // Si usamos el servicio de TEXTO
                case "texto":
                    fraw = c.getResources().openRawResource(R.raw.spacytexto);
                    brin = new BufferedReader(new InputStreamReader(fraw));
                    this.urlPath = brin.readLine() + this.analisis;
                    break;
                // Si usamos el servicio de PALABRAS
                case "palabras":
                    fraw = c.getResources().openRawResource(R.raw.spacypalabras);
                    brin = new BufferedReader(new InputStreamReader(fraw));
                    // Codificamos la palabra
                    this.urlPath =
                            brin.readLine() + "?" + this.cadena + this.analisis;
                    break;
                default:
                    fraw = null;
                    break;
            }
            if (fraw != null) {
                fraw.close();
            }
        }
        catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }
    }


    @Override
    public void run() {

        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory  cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream caInput = c.getAssets().open("holstein.crt");
            Certificate ca;
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(urlPath);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());


            switch (this.servicio) {
                // SERVICIO TEXTO
                case "texto":
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setUseCaches(false);
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setRequestProperty("Content-Type","application/json");
                    urlConnection.setRequestProperty("Host", "android.schoolportal.gr");
                    urlConnection.connect();

                    // Creamos el JSONObject con el campo texto.
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("texto", this.cadena);
                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(jsonParam.toString());
                    out.close();
                    break;

                // SERVICIO PALABRAS
                case "palabras":
                    urlConnection.setRequestMethod("GET");
                    break;
            }

            // int responseCode = con.getResponseCode();
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    response.append(inputLine);
                }

                in.close();
                // Desconectar
                urlConnection.disconnect();

                this.resultado = response.toString();
                // Procesa el resultado
                // procesaResultado(response.toString());
            }


        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Dado el resultado devuelto en la conexion.
     * Dependiendo de un servicio u otro, procesa el resultaod
     * Para TEXTO:
     *      Crea el arrayList oraciones
     * Para PALABRAS
     *      Crea morfologico para devolver el lema de la palabra
     *      Y un array de sinonimos.
     *
     */
    private void procesaResultado(String analisis) {

        try {
            JSONObject jsonObject = new JSONObject(this.resultado);
            JSONArray jsonArray;
                switch (this.servicio) {
                    case "texto":
                        switch (analisis) {
                            case "oraciones":
                                this.oraciones = new ArrayList<>();
                                jsonArray = jsonObject.getJSONArray("oraciones");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    this.oraciones.add(jsonArray.get(i).toString());
                                }
                                break;
                            case "morfologico":
                                this.texto_morfologico = new ArrayList<>();
                                jsonArray = jsonObject.getJSONArray("morfologico");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    this.texto_morfologico.add(i, new ArrayList<String>());
                                    this.texto_morfologico.get(i).add(jsonArray.getJSONObject(i).getString("lema"));
                                    this.texto_morfologico.get(i).add(jsonArray.getJSONObject(i).getString("palabra"));
                                    this.texto_morfologico.get(i).add(jsonArray.getJSONObject(i).getString("parte"));
                                }
                        }
                        break;
                    case "palabras":
                        switch (analisis) {
                            case "morfologico":
                                this.morfologico = new JSONObject(this.resultado).getJSONObject("morfologico").getString("lema");
                                break;
                            case "sinonimos":
                                this.sinonimos = new ArrayList<>();
                                jsonArray = jsonObject.getJSONArray("oraciones");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    this.sinonimos.add(jsonArray.get(i).toString());
                                }
                                break;
                        }
                        break;
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
