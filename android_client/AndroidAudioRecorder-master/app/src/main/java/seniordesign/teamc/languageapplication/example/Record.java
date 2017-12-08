package seniordesign.teamc.languageapplication.example;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import seniordesign.teamc.languageapplication.AndroidAudioRecorder;
import seniordesign.teamc.languageapplication.model.AudioChannel;
import seniordesign.teamc.languageapplication.model.AudioSampleRate;
import seniordesign.teamc.languageapplication.model.AudioSource;

public class Record extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.wav";
    public ServerInterface server = null;
    private TextView mTextView;
    private Handler mHandler;
    private String mMessage;
    public boolean wordDrawn = false;
    public boolean audioSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.server = new ServerInterface();
        this.server.filePath = this.AUDIO_FILE_PATH;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
        }

        Helper.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Helper.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
                this.audioSaved = true;
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Audio was not saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void recordAudio(View v) {
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(AUDIO_FILE_PATH)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(false)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }

    public void onClickGetWord(View v){
        setContentView(R.layout.activity_main);
        TextView wordDisplay =  (TextView)findViewById(R.id.WordDisplay);
        TextView scoreDisplay = (TextView) findViewById(R.id.AccuracyDisplay);
        Helper.requestPermission(this, Manifest.permission.INTERNET);
        this.server.wf.onClick(v);
        wordDisplay.setText(this.server.wf.word);
        this.wordDrawn = true;
        this.audioSaved = false;
        scoreDisplay.setText("Record your sound next...");
    }

    public void onClickSendAudio(View v){
        // reset the previous score
        this.server.score = null;

        // rest of work
        setContentView(R.layout.activity_main);
        TextView scoreDisplay = (TextView) findViewById(R.id.AccuracyDisplay);
        TextView wordDisplay = (TextView) findViewById(R.id.WordDisplay);
        File f = new File(this.AUDIO_FILE_PATH);
        if(!this.wordDrawn) {
            // Word has NOT been drawn
            wordDisplay.setText("Tap \"GET A WORD FROM SERVER\" first...");
        }else if(!this.audioSaved){
            // File has NOT been recorded
            wordDisplay.setText(this.server.wf.word);
            scoreDisplay.setText("Record your sound first...");
        }
        else{
            // Everything has been initialized
            Helper.requestPermission(this, Manifest.permission.INTERNET);
            this.server.onClick(v);
            while(this.server.score == null){
                System.out.println("Waiting for server to finish working with text...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            wordDisplay.setText(this.server.wf.word);
            scoreDisplay.setText(this.server.score);
        }
    }
}