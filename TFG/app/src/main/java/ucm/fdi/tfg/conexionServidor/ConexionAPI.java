package ucm.fdi.tfg.conexionServidor;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ucm.fdi.tfg.R;

public class ConexionAPI extends Thread {

    private final static String JSON = "/json/";

    private Context c;

    // Palabra buscada
    private String palabra;
    // Tipo de servicio
    private String servicio;
    // URL
    private String urlPath;

    // Variable donde se almacena el resultado
    private ArrayList<String> resultado = new ArrayList<>();


    // Para controlar errores
    private boolean tieneResultado = true;


    public ConexionAPI(Context c, String palabra, String servicio ) {

        this.c = c;
        this.servicio = servicio;
        String cadenaNormalize = Normalizer.normalize(palabra, Normalizer.Form.NFD);
        this.palabra = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");

        // Buscamos la URL del servicio
        buscarUrl();

        System.out.println(this.palabra);
    }


    public boolean tieneResultado() {
        return this.tieneResultado;
    }

    public ArrayList<String> getResultado() {
        return this.resultado;
    }



    private void buscarUrl() {
        try {
            InputStream fraw = c.getResources().openRawResource(R.raw.apiaccesibilidad);
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
            // URL A LA QUE TENEMOS QUE ACCEDER
            this.urlPath = brin.readLine() + this.servicio + JSON + URLEncoder.encode(this.palabra, "UTF-8");
            System.out.println(this.urlPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
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

            urlConnection.setRequestMethod("GET");

            int responseCode = urlConnection.getResponseCode();
            System.out.println(responseCode);
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                urlConnection.disconnect();
                procesaResultado(response.toString());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    /**
     * Dado el JSON devuelto por el servicio web.
     * Procesa el resultado dependiendo de si es
     *      Definicion
     *      Sinonimos
     *      Antonimos
     * @param res json
     */
    private void procesaResultado(String res) {

        this.resultado = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray;
            switch (this.servicio) {
                case "definicion":
                    jsonArray = jsonObject.optJSONArray("definiciones");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            this.resultado.add(jsonArray.getJSONObject(i).getString("definicion"));
                        }
                    }
                    else {
                        this.tieneResultado = false;
                    }
                    break;
                case "sinonimos":
                    jsonArray = jsonObject.optJSONArray("sinonimos");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            this.resultado.add(jsonArray.getJSONObject(i).getString("sinonimo"));
                        }
                    } else {
                      this.tieneResultado = false;
                    }
                    break;
                case "antonimos":
                    jsonArray = jsonObject.optJSONArray("antonimos");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            this.resultado.add(jsonArray.getJSONObject(i).getString("antonimo"));
                        }
                    } else {
                      this.tieneResultado = false;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}