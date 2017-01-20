package daniyar.com.contentprovider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioRecord;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button start, stop, play, increase, decrease;
    MediaRecorder recorder = null;
    String AudioSavePathInDevice = "something";
    private SoundPool soundpool;
    boolean loaded = false;
    int sound = 0;

    int bufferSize ;
    int frequency = 44100; //8000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean started = false;
    AudioRecord audioRecord;

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        start = (Button) findViewById(R.id.Start);
        stop = (Button) findViewById(R.id.Stop);
        play = (Button) findViewById(R.id.play);
        increase = (Button) findViewById(R.id.increase);
        decrease = (Button) findViewById(R.id.decrease);


        start.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);

        increase.setOnClickListener(this);
        decrease.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.share);
        ShareActionProvider shareActionProvider = new ShareActionProvider(MainActivity.this);
        shareActionProvider.setShareIntent(shareActionIntent());
        MenuItemCompat.setActionProvider(item, shareActionProvider);
        return true;
    }

    public Intent shareActionIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/mp3");
        //File f = new File(AudioSavePathInDevice);
        //Uri uri = Uri.parse("file://"+f.getAbsolutePath());;
        Uri uri = Uri.parse(AudioSavePathInDevice);
        //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
        return intent;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.Start) {
            try {
                start.setEnabled(false);
                stop.setEnabled(true);
                AudioSavePathInDevice = Environment.getExternalStorageDirectory().getPath() + "/VOICE" + "AudioRecording3.ogg"; //3gp//sdcard/audio.3gp
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                //recorder.setCaptureRate(2.0f);
                recorder.setOutputFile(AudioSavePathInDevice);
                recorder.prepare();
                recorder.start();

                audioRecord = new AudioRecord( MediaRecorder.AudioSource.MIC, frequency,
                    channelConfiguration, audioEncoding, bufferSize);
                audioRecord.startRecording();
            } catch (Exception e) {
                Log.d("error", e.getMessage());
            }
        }
        else
        if (id == R.id.Stop) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                stop.setEnabled(false);
                start.setEnabled(true);
                play.setEnabled(true);
                sound = soundpool.load(AudioSavePathInDevice, 1);
                soundpool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                        loaded = true;
                        Toast.makeText(MainActivity.this, "Recorded loaded",
                            Toast.LENGTH_LONG).show();
                    }
                });
                soundpool.load(this, sound, 1);
            } catch (Exception e) {
                Log.d("on stop", e.getMessage());
            }
         }
        else
        if (id == R.id.play) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(AudioSavePathInDevice);
                if (mediaPlayer != null) {
                    //mediaPlayer.prepare();
                    //mediaPlayer.start();
                    TextView textSpeed = (TextView) findViewById(R.id.speed);
                    String sp = textSpeed.getText().toString();
                    if (sp.equals("2.0f"))
                        playSound(2.0f);
                    if (sp.equals("1.5f"))
                        playSound(1.5f);
                    if (sp.equals("1.0f"))
                        playSound(1.0f);
                    if (sp.equals("1.1f"))
                        playSound(1.1f);
                    if (sp.equals("1.3f"))
                        playSound(1.1f);
                    if (sp.equals("0.5f"))
                        playSound(0.5f);
                }
            } catch (Exception e) {
                Log.d("playing error", e.getMessage());
            }
            Toast.makeText(MainActivity.this, "Recording Playing",
                Toast.LENGTH_LONG).show();
        }
        else {
            TextView textSpeed = (TextView) findViewById(R.id.speed);
            String sp = textSpeed.getText().toString();
            if (id == R.id.increase) {
                if (sp.equals("0.5f")) {
                    sp = "1.0f";
                    decrease.setEnabled(true);
                }
                else
                if (sp.equals("1.0f"))
                    sp = "1.1f";
                else
                if (sp.equals("1.1f"))
                    sp = "1.3f";
                else
                if (sp.equals("1.3f"))
                    sp = "1.5f";
                else
                if (sp.equals("1.5f")) {
                    sp = "2.0f";
                    increase.setEnabled(false);
                }

            }
            if (id == R.id.decrease) {
                if (sp.equals("2.0f")) {
                    sp = "1.5f";
                    findViewById(R.id.increase).setEnabled(true);
                }
                else
                if (sp.equals("1.5f"))
                    sp = "1.3f";
                else
                if (sp.equals("1.3f"))
                    sp = "1.1f";
                else
                if (sp.equals("1.1f"))
                    sp = "1.0f";
                else
                if (sp.equals("1.0f")) {
                    sp = "0.5f";
                    findViewById(R.id.decrease).setEnabled(false);
                }
            }
            textSpeed.setText(sp);
        }
    }

    public void playSound(float fSpeed) throws Exception {

//        final AudioInputStream in1 = getAudioInputStream(new File(AudioSavePathInDevice));

        AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        soundpool.play(sound, volume, volume, 1, 0, fSpeed);

////        PresetReverb mReverb = new PresetReverb(0,mediaPlayer.getAudioSessionId());//<<<<<<<<<<<<<
////        mReverb.setPreset(PresetReverb.PRESET_SMALLROOM);
////        mReverb.setEnabled(true);
////        mediaPlayer.setAuxEffectSendLevel(2.0f);
//        mediaPlayer.prepare();
//        mediaPlayer.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (! permissionToRecordAccepted) {
            finish();
        }
    }
}
