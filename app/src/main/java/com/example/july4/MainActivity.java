package com.example.july4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView resultado;
    private Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link com frond-end
        resultado = (TextView) findViewById(R.id.textView);
        enviar = (Button) findViewById(R.id.button);

        // Listener do button
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTask task = new MyTask();

                // Link da API
                String urlApi = "https://viacep.com.br/ws/40750380/json/";

                // Chamada assincrona da API
                task.execute(urlApi);
            }
        });
    }

    // Classe para chamada assíncrona da API
    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            InputStream raw_response;
            InputStreamReader response;
            BufferedReader reader;
            StringBuffer buffer = new StringBuffer();

            try {
                URL url = new URL(stringUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                raw_response = connection.getInputStream();
                response = new InputStreamReader(raw_response);
                reader = new BufferedReader(response);
                String line;

                while((line = reader.readLine())!=null) {
                    buffer.append(line);
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String retorno) {
            super.onPostExecute(retorno);
            String zipCode, streetAddress, neighborhood, city, state, areaCode;

            try {
                JSONObject jsonObject = new JSONObject(retorno);
                zipCode = jsonObject.getString("cep");
                streetAddress = jsonObject.getString("logradouro");
                neighborhood = jsonObject.getString("bairro");
                city = jsonObject.getString("localidade");
                state = jsonObject.getString("uf");
                areaCode = jsonObject.getString("ddd");

                String address;

                address = String.format("" +
                        "CEP: %s\n" +
                        "Logradouro: %s\n" +
                        "Bairro: %s\n" +
                        "Localidade: %s\n" +
                        "UF: %s\n" +
                        "DDD: %s\n" +
                        "", zipCode, streetAddress, neighborhood, city, state, areaCode);

                resultado.setText(address);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}