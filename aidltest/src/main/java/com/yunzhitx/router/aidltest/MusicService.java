package com.yunzhitx.router.aidltest;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    /**
     * 播放准备完成时
     */
    public static final String PREPARED_MESSAGE = "com.yunzhi.router.PREPARED_MESSAGE";
    private ArrayList<MusicItemEntity> musicItemEntities = new ArrayList<>();
    private MusicItemEntity musicItemEntity;
    private int currentPosition;

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        getAllNusic();
    }

    private void getAllNusic() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] musicInfo = {
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor query = contentResolver.query(uri, musicInfo, null, null, null);
                while (query.moveToNext()) {
                    MusicItemEntity musicItemEntity = new MusicItemEntity();
                    String title = query.getString(0);
                    musicItemEntity.setTitle(title);
                    String duration = query.getString(1);
                    musicItemEntity.setDuration(duration);
                    Long size = query.getLong(2);
                    musicItemEntity.setSize(size);
                    String data = query.getString(3);
                    musicItemEntity.setData(data);
                    String artist = query.getString(4);
                    musicItemEntity.setArtist(artist);
                    musicItemEntities.add(musicItemEntity);

                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("pre")) {
            String pre = intent.getStringExtra("pre");
            if (pre.equalsIgnoreCase("pre")) {
                playPre();
            } else if (pre.equalsIgnoreCase("next")) {
                playNext();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 打开第几个音乐
     *
     * @param position
     */

    public void openMusic(int position) throws IOException {
        currentPosition = position;
        musicItemEntity = musicItemEntities.get(position);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play();
                notifyChange(PREPARED_MESSAGE);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MusicService.this, "isError", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mediaPlayer.setDataSource(musicItemEntity.getData());

        mediaPlayer.prepareAsync();

    }

    private void notifyChange(String preparedMessage) {
        Intent intent = new Intent();
        intent.setAction(preparedMessage);
        intent.setPackage("com.yunzhitx.router.aidltest");
        sendBroadcast(intent);
    }

    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private String getArtist() {
        if (musicItemEntity != null) {
            return musicItemEntity.getArtist();
        }
        return "";
    }

    private String name() {
        if (musicItemEntity != null) {
            return musicItemEntity.getTitle();
        }
        return "";
    }

    private int getDuration() {
        if (musicItemEntity != null) {
            return Integer.parseInt(musicItemEntity.getDuration());
        }
        return 0;
    }

    private int getCurrentDur() {
        return 0;
    }

    private void seekTo(int position) {

    }

    private void setPlayMode(int mode) {

    }

    private void playPre() {
        if (musicItemEntity != null) {
            if (currentPosition - 1 > 0) {
                currentPosition = currentPosition - 1;
                try {
                    iBinder.openMusic(currentPosition);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    iBinder.openMusic(0);
                    currentPosition = 0;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            NotificationUtils notificationUtils = new NotificationUtils(musicItemEntities, currentPosition, getApplicationContext());
            notificationUtils.createNotification();
        }
    }

    private void playNext() {
        if (musicItemEntity != null) {
            if (musicItemEntities.size() > currentPosition + 1) {
                currentPosition = currentPosition + 1;
                try {
                    iBinder.openMusic(currentPosition);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    iBinder.openMusic(0);
                    currentPosition = 0;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            NotificationUtils notificationUtils = new NotificationUtils(musicItemEntities, currentPosition, getApplicationContext());
            notificationUtils.createNotification();
        }
    }

    private boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    private IMusicServer.Stub iBinder = new IMusicServer.Stub() {
        MusicService musicService = MusicService.this;

        @Override
        public void openMusic(int position) throws RemoteException {
            try {
                musicService.openMusic(position);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void play() throws RemoteException {
            musicService.play();
        }

        @Override
        public void pause() throws RemoteException {
            musicService.pause();
        }

        @Override
        public String getArtist() throws RemoteException {
            return musicService.getArtist();
        }

        @Override
        public String name() throws RemoteException {
            return musicService.name();
        }

        @Override
        public int getDuration() throws RemoteException {
            return musicService.getDuration();
        }

        @Override
        public int getCurrentDur() throws RemoteException {
            return musicService.getCurrentDur();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            musicService.seekTo(position);
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            musicService.setPlayMode(mode);
        }

        @Override
        public void playPre() throws RemoteException {
            musicService.playPre();
        }

        @Override
        public void playNext() throws RemoteException {
            musicService.playNext();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return musicService.isPlaying();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
}
