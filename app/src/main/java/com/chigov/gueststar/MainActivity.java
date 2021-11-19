package com.chigov.gueststar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private ImageView imageStar;

    private String url = "https://ru.wikipedia.org/wiki/100_%D1%81%D0%B0%D0%BC%D1%8B%D1%85_%D0%B2%D0%BB%D0%B8%D1%8F%D1%82%D0%B5%D0%BB%D1%8C%D0%BD%D1%8B%D1%85_%D0%BB%D1%8E%D0%B4%D0%B5%D0%B9_%D0%B2_%D0%B8%D1%81%D1%82%D0%BE%D1%80%D0%B8%D0%B8_(%D0%BA%D0%BD%D0%B8%D0%B3%D0%B0)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageStar = findViewById(R.id.imageStar);
        getContent();
    }

    //создаем два класса - для загрузки изображений и контента
    private static class DownloadContentTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                //открываем соединение
                urlConnection = (HttpURLConnection) url.openConnection();
                //получаем поток ввода
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                //начинаем читать построчно
                String line = reader.readLine();
                //будем читать до тех пор пока строка не будет равна нулю
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                //когда чтение будет закончено возвращаем
                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                //открываем соединение
                urlConnection = (HttpURLConnection) url.openConnection();
                //получаем поток ввода
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
    //создали классы - создаем метод, которые будут возвращать контент
    private void getContent(){
        DownloadContentTask task = new DownloadContentTask();
        try {
            String content  = task.execute(url).get();
            Log.i("test",content);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}