// IMusicServer.aidl
package com.yunzhitx.router.aidltest;

// Declare any non-default types here with import statements

interface IMusicServer {
boolean isPlaying();
void openMusic(int position);
void play();
void pause();
String getArtist();
String name();
int getDuration();
int getCurrentDur();
void seekTo(int position);
void setPlayMode(int mode);
void playPre();
void playNext();
}
