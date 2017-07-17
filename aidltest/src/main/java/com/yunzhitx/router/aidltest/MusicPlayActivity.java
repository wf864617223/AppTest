package com.yunzhitx.router.aidltest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicPlayActivity extends AppCompatActivity {

    ArrayList<MusicItemEntity> musicItem = new ArrayList<>();
    private int position;
    private TextView tvMusicName;
    private TextView tvMusicArtist;
    private TextView tvMusicDuation;
    private Button btnPlay;
    private Button btnPre;
    private Button btnNext;
    private boolean isPlay;
    private MyBroadCastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initData();
        tvMusicName = (TextView) findViewById(R.id.tv_music_name);
        tvMusicArtist = (TextView) findViewById(R.id.tv_music_artist);
        tvMusicDuation = (TextView) findViewById(R.id.tv_music_duration);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnNext = (Button) findViewById(R.id.btn_next);
        Bundle extras = getIntent().getExtras();
        musicItem = extras.getParcelableArrayList("musicItem");
        position = extras.getInt("position", 0);
        //Toast.makeText(this,  title+"", Toast.LENGTH_SHORT).show();
        bindServices();
        setEvent();
        NotificationUtils notificationUtils = new NotificationUtils(musicItem,position,this);
        notificationUtils.createNotification();
        //createNotification();
    }

    /*private void createNotification() {
        String title = musicItem.get(position).getTitle();
        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher_round;
        notification.tickerText = title;
        notification.when = System.currentTimeMillis();
        Intent intent = new Intent(this,MusicPlayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.tv_title,title);
        remoteViews.setTextViewText(R.id.tv_info,musicItem.get(position).getArtist());
        remoteViews.setImageViewResource(R.id.iv_img,R.mipmap.ic_launcher_round);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentView = remoteViews;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2,notification);
    }*/

    private void initData() {
        //注册监听准备好的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.PREPARED_MESSAGE);
        receiver = new MyBroadCastReceiver();
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消广播
        unregisterReceiver(receiver);
        receiver = null;
    }

    private void setEvent() {
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iMusicServer.playPre();
                    if(position-1>=0){
                        position = position-1;
                        /*NotificationUtils notificationUtils = new NotificationUtils(musicItem,position,MusicPlayActivity.this);
                        notificationUtils.createNotification();*/
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iMusicServer.playNext();
                    if(position+1<=musicItem.size()){
                        position = position+1;
                        /*NotificationUtils notificationUtils = new NotificationUtils(musicItem,position,MusicPlayActivity.this);
                        notificationUtils.createNotification();*/
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isPlay) {
                        iMusicServer.pause();
                    } else {
                        iMusicServer.play();
                    }
                    setButtonStatus();
                    isPlay = !isPlay;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void setButtonStatus() {
        if (isPlay) {
            btnPlay.setText("播放");
        } else {
            btnPlay.setText("暂停");
        }
    }

    private IMusicServer iMusicServer;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMusicServer = IMusicServer.Stub.asInterface(service);
            if (iMusicServer != null) {
                try {
                    iMusicServer.openMusic(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMusicServer = null;
        }
    };

    private void bindServices() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.setAction("com.yunzhitx.router.aidltest.MusicService");
        intent.setPackage("com.yunzhitx.router.aidltest");
        intent.putExtras(bundle);

        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
    private class MyBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //设置view状态
            try {
                setViewStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void setViewStatus() throws RemoteException {
        tvMusicArtist.setText(iMusicServer.getArtist());
        tvMusicDuation.setText(iMusicServer.getDuration()+"");
        tvMusicName.setText(iMusicServer.name());
        isPlay = iMusicServer.isPlaying();
        setButtonStatus();
    }
}
