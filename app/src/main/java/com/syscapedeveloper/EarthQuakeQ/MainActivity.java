package com.syscapedeveloper.EarthQuakeQ;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    String urlString = "https://data.bmkg.go.id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_main);

        Button refreshBtn = (Button)findViewById(R.id.refreshData);
        TextView textNarasi = (TextView)findViewById(R.id.Narasi);
        ImageView imageView = (ImageView) findViewById(R.id.gambar);
        textNarasi.setText("Menyiapkan Data ...");


        try {

            JSONObject res  = getJSONObjectFromURL();
            GeneratetextInfo(res,textNarasi);
            LoadImageFromWebOperations("https://data.bmkg.go.id/DataMKG/TEWS/"+res.getJSONObject("Infogempa").getJSONObject("gempa").getString("Shakemap"),imageView);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject res =  getJSONObjectFromURL();
                            GeneratetextInfo(res,textNarasi);
                            LoadImageFromWebOperations("https://data.bmkg.go.id/DataMKG/TEWS/"+res.getJSONObject("Infogempa").getJSONObject("gempa").getString("Shakemap"),imageView);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).run();
            }
        });
    }

    void GeneratetextInfo(JSONObject res , TextView textNarasi){
        String result="+ Tanggal Kejadian : "+ConsolidateIEarthQuackeInfo(res, "Tanggal","");
        result+="\n+ Jam Kejadian : "+ConsolidateIEarthQuackeInfo(res, "Jam","");
        result+="\n+ Magintudo : "+ConsolidateIEarthQuackeInfo(res, "Magnitude"," Skala Richter");
        result+="\n+ Kedalaman : "+ConsolidateIEarthQuackeInfo(res, "Kedalaman","");
        result+="\n+ Koordinat gempa : "+ConsolidateIEarthQuackeInfo(res, "Coordinates","");
        result+="\n+ Wilayah :"+ConsolidateIEarthQuackeInfo(res, "Wilayah","");
        result+="\n+ Gempa dirasakan diwilayah : "+ConsolidateIEarthQuackeInfo(res, "Dirasakan","");
        result+="\n+ Potensi : "+ConsolidateIEarthQuackeInfo(res, "Potensi","");
        textNarasi.setText(result);
    }


    String ConsolidateIEarthQuackeInfo(JSONObject res, String Key,String optional ){
        String result = "";
        try {
            result =  res.getJSONObject("Infogempa").getJSONObject("gempa").getString(Key)+" "+optional;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    void LoadImageFromWebOperations(String src, ImageView imageView) throws MalformedURLException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(src);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bmp);
            }
        }).run();

    }


    JSONObject getJSONObjectFromURL() throws IOException, JSONException, IOException, JSONException {




        HttpURLConnection urlConnection = null;
        URL url = new URL(this.urlString+"/DataMKG/TEWS/autogempa.json");
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000  );
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoOutput(true);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);
        return new JSONObject(jsonString);
    }
}