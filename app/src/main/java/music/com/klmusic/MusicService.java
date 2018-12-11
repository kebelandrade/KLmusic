package music.com.klmusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {



    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;


    @Override
    public void onCreate() {
        super.onCreate();
        songPosn=0;
//create player
        player = new MediaPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        initMusicPlayer();
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public  class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    //sobreescribi de aqui
    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }



    //hasta aqui


    public void playSong(){
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
//get id
        long currSong = playSong.getID();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);


        try {
            player.setDataSource(getApplicationContext(),trackUri);
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("MUSIC SERVICE","Error setting data source",e);
        }

        player.prepareAsync();

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        //now we start playback
        mp.start();
    }


    public void setSongs(int songIndex){
        songPosn=songIndex;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "El servicio ha sido comenzado", Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.CANAL_PRINCIPAL)
                .setContentTitle("Titulo de la Notif")
                .setContentText("La descripcion de la notif")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notif = builder.build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1024, notif);
        return super.onStartCommand(intent, flags, startId);
    }

    private final IBinder musicBind = new MusicBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
