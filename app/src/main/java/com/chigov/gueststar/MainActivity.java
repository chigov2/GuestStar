package com.chigov.gueststar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private ImageView imageStar;

    private String url = "https://www.ivi.ru/titr/motor/best-actors-of-the-21st-century";
    private String siteNameUrl = "https://www.ivi.ru";


    private ArrayList<String> urls;
    private ArrayList<String> urls2;
    private ArrayList<String> names;

    private int numberOfQuestion;
    private int numberOfRightAnswer;

    //все кнопки в один массив
    private ArrayList<Button> buttons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        buttons = new ArrayList<>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        imageStar = findViewById(R.id.imageStar);
        names = new ArrayList<>();
        urls = new ArrayList<>();
        urls2 = new ArrayList<>();
        getContent();
        playGame();
    }

    public void onClickAnswer(View view) {
        //playGame();
        //в переменную button записываем картинку с приведением типа
        Button button = (Button) view;
        String tag = button.getTag().toString();
        if (Integer.parseInt(tag) ==numberOfRightAnswer){
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Not correct. Correct answer: " + names.get(numberOfQuestion), Toast.LENGTH_SHORT).show();
        }
        playGame();

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
            String start ="Сегодня мы решили посмотреть на красивых и безумно талантливых мужчин-актеров. Очень долго думали, сравнивали, делились впечатлениями и все же остановились на 25 самых-самых. Дабы никого не обидеть, имена указаны в алфавитном порядке.";
            //String finish = "18 июля 2017";
            //String finish = ">Элайджа Вуд</a>";
            String finish = ">18 июля 2017</time>";
            Pattern pattern = Pattern.compile(start + "(.*?)" + finish);
            Matcher matcher = pattern.matcher(content);
            String splitContent = "";
            while(matcher.find()){
                splitContent = matcher.group(1);
            }
            Pattern patternImg = Pattern.compile("<img src=\"(.*?)\"");
            Pattern patternName = Pattern.compile("</a> /(.*?)</span><p>");


            Matcher matcherImg = patternImg.matcher(splitContent);
            Matcher matcherName = patternName.matcher(splitContent);

            //данные необходимо поместить в массивы

            while(matcherName.find()){
                names.add(matcherName.group(1));
            }

            while(matcherImg.find()){
                urls.add(matcherImg.group(1));
            }
            for (String t : names){
               Log.i("test",t);
            }
            for (String link : urls){
                String p =siteNameUrl +link;
                Log.i("test",p);
                urls2.add(p);
            }
//            for(String i :urls2){
//                Log.i("test",i);
//            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playGame(){
     //необходимо наугад выбрать фото звезды - для этого будет вызываться метод generateQuestion
        generateQuestion();

        //в зависимости от сгенерированного номера получить картинку
        DownloadImageTask task = new DownloadImageTask();
        try {
            Bitmap bitmap = task.execute(urls2.get(numberOfQuestion)).get();
            if (bitmap != null)
            {
                //Log.i("test","OK");
                imageStar.setImageBitmap(bitmap);

                //создаем цикл, чтобы у кнопок устанавливать текст
                for (int i = 0; i < buttons.size(); i++){
                    //
                    if (i == numberOfRightAnswer){
                        buttons.get(i).setText(names.get(numberOfQuestion));
                    }else{
                        int wrongAnswer = generateWrongAnswer();
                        buttons.get(i).setText(names.get(wrongAnswer));
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void generateQuestion(){
        //номер вопроса должен быть случайным числом в массиве names
        numberOfQuestion = (int)(Math.random() * names.size());
        //numberOfRightAnswer - должен быть установлен правильный ответ
        numberOfRightAnswer = (int)(Math.random() * buttons.size());
    }
    //номер неправильного ответа
    private int generateWrongAnswer(){
        return (int)(Math.random() * names.size());
    }

}