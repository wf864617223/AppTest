package com.yunzhitx.router.aidltest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 2flower on 2017/7/7.
 */

public class MusicItemEntity  implements Parcelable{
    private String title;
    private String duration;
    private long size;
    private String data;
    private String artist;

    public MusicItemEntity() {
    }

    protected MusicItemEntity(Parcel in) {
        title = in.readString();
        duration = in.readString();
        size = in.readLong();
        data = in.readString();
        artist = in.readString();
    }

    public static final Creator<MusicItemEntity> CREATOR = new Creator<MusicItemEntity>() {
        @Override
        public MusicItemEntity createFromParcel(Parcel in) {
            return new MusicItemEntity(in);
        }

        @Override
        public MusicItemEntity[] newArray(int size) {
            return new MusicItemEntity[size];
        }
    };

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(duration);
        dest.writeLong(size);
        dest.writeString(data);
        dest.writeString(artist);
    }
}
