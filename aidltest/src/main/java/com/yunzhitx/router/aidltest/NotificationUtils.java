package com.yunzhitx.router.aidltest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Created by 2flower on 2017/7/11.
 */

public class NotificationUtils {
    ArrayList<MusicItemEntity> musicItem = new ArrayList<>();
    private int position;
    Context context;

    public NotificationUtils(ArrayList<MusicItemEntity> musicItem, int position, Context context) {
        this.musicItem = musicItem;
        this.position = position;
        this.context = context;
    }

    public void createNotification() {
        String title = musicItem.get(position).getTitle();
        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher_round;
        notification.tickerText = title;
        notification.when = System.currentTimeMillis();
        //Intent intent = new Intent(context,MusicPlayActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent();
        intent.setAction("com.yunzhitx.router.aidltest.MusicService");
        intent.setPackage("com.yunzhitx.router.aidltest");
        intent.putExtra("pre","pre");
        PendingIntent pendingIntent = PendingIntent.getService(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent();
        intent2.setAction("com.yunzhitx.router.aidltest.MusicService");
        intent2.setPackage("com.yunzhitx.router.aidltest");
        intent2.putExtra("pre","next");
        PendingIntent pendingIntent2 = PendingIntent.getService(context,2,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.tv_title,title);
        remoteViews.setTextViewText(R.id.tv_info,musicItem.get(position).getArtist());
        remoteViews.setImageViewResource(R.id.iv_img,R.mipmap.ic_launcher_round);
        remoteViews.setOnClickPendingIntent(R.id.btn_pre,pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn_next,pendingIntent2);
        //PendingIntent pendingIntent2 = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentView = remoteViews;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2,notification);
    }
}
