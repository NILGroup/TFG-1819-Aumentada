package ucm.fdi.tfg;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
import android.content.Context;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ConexionSpacy extends Thread {

    private String texto;
    private String tipo_servicio;
    private String json_result = "";
    private ArrayList<String> resultado = new ArrayList<String>();
    private Context context ;

    public ConexionSpacy(String word, String T, Context c) {
        this.texto = word;
        this.tipo_servicio  = T;
        this.context = c;
    }

    public ArrayList<String> getResultado() {
        return this.resultado;
    }


    @Override
    public void run() {

        String url = "";

        switch (this.tipo_servicio) {
            case "morfologico":
                url = "https://holstein.fdi.ucm.es/nlp-api/analisis/";
                break;
            case "gramatical":
                url = "https://holstein.fdi.ucm.es/nlp-api/analisis";
                break;

        }

        url += this.texto;

        URL obj = null;
        try {
            obj = new URL(url);

            //Intento de conectar al servicio https

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(context.getAssets().open("certificado.crt"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.d("SSL","ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

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
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);


            //Intento de conectar al servicio https

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            if(this.tipo_servicio == "morfologico") {
                con.setRequestMethod("GET");
                System.out.println(this.tipo_servicio);
            }else
                con.setRequestMethod("POST");

            int responseCode = con.getResponseCode();
            System.out.println(responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;

            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                response.append(inputLine);
            }

            in.close();

            System.out.println(response.toString());

            con.disconnect();

            this.json_result = response.toString();

            //procesaResultado();

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
/*
    private void procesaResultado(){

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();

        switch (this.tipo_servicio) {
            case "dificultad":
                try {
                    JsonElement datos = parser.parse(this.json_result);
                    //Obtenemos images
                    JsonObject jobject = datos.getAsJsonObject();
                    JsonArray arraySinonimos = jobject.getAsJsonArray("palabraSencilla");
                    // System.out.println("sinonimos" + arraySinonimos);

                    jobject = arraySinonimos.get(0).getAsJsonObject();
                    JsonPrimitive s = jobject.getAsJsonPrimitive("sinonimo");
                    resultado.add(s.getAsString().replaceAll("\"", ""));

                } catch (Exception e) {
                    // TODO: handle exception
                }

                break;
            case "sinonimos":
                try {
                    JsonElement datos = parser.parse(this.json_result);
                    //Obtenemos images
                    JsonObject jobject = datos.getAsJsonObject();
                    JsonArray arraySinonimos = jobject.getAsJsonArray("sinonimos");
                    // System.out.println("sinonimos" + arraySinonimos);

                    for (JsonElement sinonimo : arraySinonimos) {
                        jobject = sinonimo.getAsJsonObject();
                        JsonPrimitive s = jobject.getAsJsonPrimitive("sinonimo");
                        resultado.add(s.getAsString().replaceAll("\"", ""));
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

                break;
            case "antonimos":
                try {
                    JsonElement datos = parser.parse(this.json_result);
                    //Obtenemos images
                    JsonObject jobject = datos.getAsJsonObject();
                    JsonArray arrayAntonimos = jobject.getAsJsonArray("antonimos");
                    // System.out.println("sinonimos" + arrayAntonimos);

                    for (JsonElement antonimo : arrayAntonimos) {
                        jobject = antonimo.getAsJsonObject();
                        JsonPrimitive s = jobject.getAsJsonPrimitive("antonimo");
                        resultado.add(s.getAsString().replaceAll("\"", ""));
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

                break;
            case "definicion":
                try {
                    JsonElement datos = parser.parse(this.json_result);
                    //Obtenemos images
                    JsonObject jobject = datos.getAsJsonObject();
                    JsonArray arrayDefiniciones = jobject.getAsJsonArray("definiciones");
                    // System.out.println("sinonimos" + arrayAntonimos);

                    for (JsonElement definicion : arrayDefiniciones) {
                        jobject = definicion.getAsJsonObject();
                        JsonPrimitive s = jobject.getAsJsonPrimitive("definicion");
                        resultado.add(s.getAsString().replaceAll("\"", ""));
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
                break;
        }

    }
    */
}
