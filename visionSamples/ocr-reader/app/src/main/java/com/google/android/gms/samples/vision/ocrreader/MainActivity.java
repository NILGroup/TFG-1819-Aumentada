/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    private Button button;

    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";

    private static final int MENU_TRADUCCION = 0x42;
    private static final int MENU_DEFINICION = 0x44;
    private static final int MENU_ITEM_ID = 0x46;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView)findViewById(R.id.status_message);
        textValue = (TextView)findViewById(R.id.text_value);
        button = (Button) findViewById(R.id.b_rest);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_text).setOnClickListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min, max;
                String selectedText;
                //Button btnSendCode = (Button) v;
                //TextView txtResult = (TextView) findViewById(R.id.txtResult);
                String url = "http://sesat.fdi.ucm.es:8080/servicios/rest/palabras/json/";
                String respuesta = "";
                min = 0;
                max = textValue.getText().length();
                if (textValue.isFocused()) {
                    final int selStart = textValue.getSelectionStart();
                    final int selEnd = textValue.getSelectionEnd();

                    min = Math.max(0, Math.min(selStart, selEnd));
                    max = Math.max(0, Math.max(selStart, selEnd));
                }
                // Perform your definition lookup with the selected text
                selectedText = textValue.getText().subSequence(min, max).toString();
                url = url + selectedText;
                textValue.setText(url);


                ConnectivityManager connMgr = ( ConnectivityManager ) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected())

                {
                    URL dir = null;
                    try {
                         dir = new URL(url);
                    } catch (MalformedURLException e) {
                        textValue.setText("URL");
                    }
                    BufferedReader in = null;
                    try {
                        assert dir != null;
                        HttpURLConnection urlConnection = (HttpURLConnection) dir.openConnection();
                        int code = urlConnection.getResponseCode();
                        textValue.setText(code);
                      /*  InputStream stream = urlConnection.getErrorStream();
                        if (stream == null) {
                            stream = urlConnection.getInputStream();
                        }
                        in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));*/
                    }catch (IOException e) {
                        textValue.setText("URL/IOE");
                    }/*
                    String inputLine = "";
                    StringBuffer response = new StringBuffer();*/

                 /*   try {
                        inputLine = in.readLine();
                    } catch (IOException e) {
                        textValue.setText("Buffer/IOE");
                    }
                    respuesta = inputLine;

                    try {
                        in.close();
                    } catch (IOException e) {
                        textValue.setText("Cierre/IOE");
                    }

                    JSONObject resObject = null;
                    try {
                        resObject = new JSONObject(respuesta);
                    } catch (JSONException e) {
                        textValue.setText("JSON 1");
                    }
                    try {
                        textValue.setText(resObject.getString("Result"));
                    } catch (JSONException e) {
                        textValue.setText("JSON 2");
                    }*/
                }else
                    textValue.setText("Sin conexión a Internet");
            }
        });
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }

    /**
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
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    textValue.setText(text);
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
    }
}
