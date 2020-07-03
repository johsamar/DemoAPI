package com.josamar.demoapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.josamar.demoapi.model.WeatherInfo;

import java.net.MalformedURLException;
import java.net.URL;

import cafsoft.foundation.Data;
import cafsoft.foundation.DataTaskCompletionHandler;
import cafsoft.foundation.Error;
import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLRequest;
import cafsoft.foundation.URLResponse;
import cafsoft.foundation.URLSession;

public class MainActivity extends AppCompatActivity {
    private final String host = "http://api.openweathermap.org/";
    private final String service= "data/2.5/weather";
    private final String key = "4b8908ce9f2ed990a88392386aac7461";
    private final String unit = "&units=metric";

    private TextView codeISO = null;
    private TextView cityName = null;
    private TextView temperatura = null;
    private TextView actual = null;
    private TextView minima = null;
    private TextView maxima = null;
    private TextView setActual = null;
    private TextView setMinima = null;
    private TextView setMaxima = null;

    private ImageView icon = null;
    private EditText codeIn= null;
    private EditText cityIn= null;

    private Button getIn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
    }
    private void initViews(){
        codeISO = findViewById(R.id.lblCode);
        cityName = findViewById(R.id.lblCity);
        temperatura = findViewById(R.id.lblTemp);
        actual = findViewById(R.id.lblActual);
        minima = findViewById(R.id.lblMin);
        maxima = findViewById(R.id.lblMax);
        setActual = findViewById(R.id.lblSetActual);
        setMinima = findViewById(R.id.lblSetMin);
        setMaxima = findViewById(R.id.lblSetMax);
        icon = findViewById(R.id.icons);
        codeIn= findViewById(R.id.inputISO);
        cityIn= findViewById(R.id.inputCity);

        getIn = findViewById(R.id.btnInfo);

    }
    private void initEvents(){
        getIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                resultados();
            }
        });
    }
    private void resultados(){
        String iso = codeIn.getText().toString();
        String city = cityIn.getText().toString();
        if(iso.length()>0 && city.length()>0){
            getInfo(iso,city);
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.fields_empty));
            alert.setPositiveButton("Ok", null);
            alert.show();
        }
    }
    public void getInfo(String countryIso, String cityName) {
        String strUrl = host + service + "/?appid=" + key + unit;
        String query = strUrl + "&q=" + cityName + "," + countryIso;
        URL url = null;
        try {
            url = new URL(query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLRequest request = new URLRequest(url);
        URLSession.getShared().dataTask(request, (data, response, error) -> {
            HTTPURLResponse resp = (HTTPURLResponse) response;
            if (resp.getStatusCode() == 200) {
                String text = data.toText();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                Gson json = gsonBuilder.create();
                WeatherInfo wInfo = json.fromJson(text,WeatherInfo.class);
                showInfo(wInfo);
            }
        }).resume();
    }
    public void showInfo(WeatherInfo root){
        runOnUiThread(()->{
            setActual.setText(String.valueOf(root.getMain().getTemp())+"°C");
            setMinima.setText(String.valueOf(root.getMain().getTempMin())+"°C");
            setMaxima.setText(String.valueOf(root.getMain().getTempMax())+"°C");
        });
    }
}
