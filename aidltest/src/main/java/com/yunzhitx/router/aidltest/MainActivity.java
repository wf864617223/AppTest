package com.yunzhitx.router.aidltest;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lvMusic;
    private ArrayList<MusicItemEntity> musicItemEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvMusic = (ListView) findViewById(R.id.lv_music);
        musicAdapter = new MusicAdapter();
        getMusic();
        lvMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,MusicPlayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("musicItem",musicItemEntities);
                bundle.putInt("position",position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private MusicAdapter musicAdapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    lvMusic.setAdapter(musicAdapter);
                    musicAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private void getMusic() {
        new Thread(){
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
                Cursor query = contentResolver.query(uri,musicInfo,null,null,null);
                while (query.moveToNext()){
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
                handler.sendEmptyMessage(100);
            }
        }.start();
    }
    class MusicAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return musicItemEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return musicItemEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            holder holders = null;
            if(convertView==null){
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_music,null);
                holders = new holder(convertView);
                convertView.setTag(holders);
            }else{
                holders = (holder) convertView.getTag();
            }
            holders.tvTitle.setText(musicItemEntities.get(position).getTitle()+"");
            holders.tvArtist.setText(musicItemEntities.get(position).getArtist()+"");
            holders.tvSize.setText(musicItemEntities.get(position).getSize()+"");
            return convertView;
        }
        class holder{
            TextView tvTitle;
            TextView tvArtist;
            TextView tvSize;
            public holder(View view){
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvArtist = (TextView) view.findViewById(R.id.tv_artist);
                tvSize = (TextView) view.findViewById(R.id.tv_size);
            }
        }
    }
}
