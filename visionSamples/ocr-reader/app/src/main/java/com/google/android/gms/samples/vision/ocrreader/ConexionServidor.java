package com.google.android.gms.samples.vision.ocrreader;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class ConexionServidor extends Thread{

    private String text;
    public String text_result = "123";

    public ConexionServidor(String word) {
        this.text = word;
    }


    @Override
    public void run() {
        String url = "http://sesat.fdi.ucm.es:8080/servicios/rest/sinonimos/json/";

        url += this.text;

        URL obj = null;
        try {
            obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

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

            this.text_result = response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
