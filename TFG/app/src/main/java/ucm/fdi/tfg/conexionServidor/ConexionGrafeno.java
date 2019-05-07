package ucm.fdi.tfg.conexionServidor;

import android.content.Context;
import android.util.Log;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ucm.fdi.tfg.R;

public class ConexionGrafeno extends Thread {

    private Context c;

    // Texto sin resumir
    private String cadena;
    // URL del servicio web
    private String urlPath;

    // Resumen del texto
    private String resumen;


    public ConexionGrafeno(Context c, String cadena) {
        this.c = c;
        this.cadena = cadena;

        // Buscamos la url
        getUrl(c);
    }


    /**
     * Devuelve el resumen
     * @return resumen
     */
    public String getResumen() {
        return this.resumen;
    }


    /***
     * Dependiendo del servicio de Spacy que queramos utilizar, buscará su correspondiente URL
     * @param c Context
     */
    private void getUrl(Context c) {
        try {
            InputStream fraw = c.getResources().openRawResource(R.raw.grafeno);
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
            this.urlPath = brin.readLine();
            fraw.close();
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
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream caInput = c.getAssets().open("sesat.crt");
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
            jsonParam.put("text", this.cadena);
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

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

                // Procesa el resultado
                procesaResultado(response.toString());
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
     * @param res resultado json
     */
    private void procesaResultado(String res) {

        try {
            this.resumen = new JSONObject(res).getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
