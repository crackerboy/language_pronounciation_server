package seniordesign.teamc.languageapplication.example;

/**
 * Created by v3nd3774 on 12/1/2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.*;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordFactory extends Activity implements Callback, View.OnClickListener{

    public List<String> words;
    public int wordChoice;
    public int numWords;
    public Random rand;
    public String word;
    public static String words_url = "http://174.138.55.47/app/words";

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        findViewById(R.id.GetButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.GetButton) {
            if(this.numWords == 0 && this.word == "None"){
                // try again to get the words from the server
                this.getWords();
            }else{
                this.drawWord();
            }
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        //do something to indicate error
        e.printStackTrace();
        this.numWords = 0;
        this.word = "None";
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            // Convert the response to JSON and return
            String jsonData = null;
            JSONObject responseObject = null;
            JSONArray responseArray = null;
            try {
                jsonData = response.body().string();
                responseObject = new JSONObject(jsonData);
                responseArray = responseObject.getJSONArray("words");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Store words in class from response object
            if (responseArray != null) {
                for (int i=0;i<responseArray.length();i++){
                    try {
                        this.words.add(responseArray.getString(i));
                        this.numWords++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            this.drawWord();
        }
    }

    public void drawWord(){
        int choice = this.drawWordIndex();
        String word;
        if(choice == -1){
            word = "None";
        }else{
            word = this.words.get(choice);
        }
        this.word = word;
    }

    public int drawWordIndex(){
        int choice;
        if(this.numWords == 0){
            // No words yet
            choice = -1;
        }else{
            // Words are there
            choice = this.rand.nextInt(this.numWords  + 1);
        }
        return choice;
    }

    public WordFactory(){
        this.words = new ArrayList<>();
        this.rand = new Random();
        this.getWords();
    }

    public void getWords(){
        //Retrieves words from MYSQL database and saves them as well as the number of words to public class vars.
        // Not initialized
        OkHttpClient okHttpClient = new OkHttpClient();

        // Create a GET request to send the data to UPLOAD_URL
        Request request = new Request.Builder()
                .url(this.words_url)
                .get()
                .header("Accept-Encoding", "gzip, deflate") // Exactly like Python test file headers
                .header("Connection", "keep-alive") // Ditto
                .build();

        okHttpClient.newCall(request).enqueue(this);
    }
}
