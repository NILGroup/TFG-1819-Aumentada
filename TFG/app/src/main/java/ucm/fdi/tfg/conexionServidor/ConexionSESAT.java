package ucm.fdi.tfg.conexionServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

public class ConexionSESAT extends Thread {

    private String texto;
    private String tipo_servicio;
    private String json_result = "";
    private ArrayList<String> resultado = new ArrayList<>();

    public ConexionSESAT(String word, String T) {
        this.texto = word;
        this.tipo_servicio  = T;
    }

    public ArrayList<String> getResultado() {
        return this.resultado;
    }


    @Override
    public void run() {

        String url = "";

        switch (this.tipo_servicio) {
            case "dificultad":
                url = "http://sesat.fdi.ucm.es:8080/servicios/rest/palabras/json/";
                break;
            case "sinonimos":
                url = "http://sesat.fdi.ucm.es:8080/servicios/rest/sinonimos/json/";
                break;
            case "antonimos":
                url = "http://sesat.fdi.ucm.es:8080/servicios/rest/antonimos/json/";
                break;
            case "definicion":
                url = "http://sesat.fdi.ucm.es:8080/servicios/rest/definicion/json/";
                break;
        }

        url += this.texto;

        URL obj;
        try {
            obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println(responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;

            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            con.disconnect();

            this.json_result = response.toString();

            procesaResultado();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void procesaResultado(){

        // Gson gson = new Gson();
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
}