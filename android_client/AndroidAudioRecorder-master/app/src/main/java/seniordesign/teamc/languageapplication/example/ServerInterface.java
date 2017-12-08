package seniordesign.teamc.languageapplication.example;

/**
 * Created by v3nd3774 on 12/1/2017.
 */


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerInterface extends Activity implements Callback, View.OnClickListener{
    public static String host = "http://174.138.55.47/app";
    public static String api = "http://174.138.55.47/app/api";
    public WordFactory wf = new WordFactory();
    public String filePath;
    public String score;
    public JSONObject responseJSON = null;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        findViewById(R.id.CompareButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.CompareButton) {
            // Builds post request and sends to server in child thread
            this.queryServer(this.wf.word, new File(this.filePath));
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        //do something to indicate error
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            // Convert the response to JSON and return
            String jsonData = null;
            JSONObject responseObject = null;
            try {
                jsonData = response.body().string();
                responseObject = new JSONObject(jsonData);
                this.responseJSON = responseObject;
                this.score = this.responseJSON.get("score").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void queryServer   (String word, File file){
        byte[] data = new byte[(int)file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(data);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        // Send the passed in (word,file) tuple to the server and return the json response.
        String UPLOAD_URL = this.api;

        // Create an HTTP client to execute the request
        OkHttpClient client = new OkHttpClient();

        // Create a multipart request body. Add metadata and files as 'data parts'.
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio_file", file.getName(),
                        RequestBody.create((MediaType)null, data))
                .addFormDataPart("word", word)
                .build();

        // Create a POST request to send the data to UPLOAD_URL
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .header("Accept-Encoding", "gzip, deflate") // Exactly like Python test file headers
                .header("Connection","keep-alive") // Ditto
                .build();

        client.newCall(request).enqueue(this);
    }

    public int contactServer(){
        // returns http response code or -1 on failure
        int code;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.host)
                .build();
        Response httpResponse = null;
        try {
            httpResponse = client.newCall(request).execute();
            code = httpResponse.code();
        } catch (Exception e) {
            code = -1;
        }
        return code;
    }

}
