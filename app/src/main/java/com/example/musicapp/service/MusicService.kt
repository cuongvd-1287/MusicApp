package com.example.musicapp.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.musicapp.R
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.notification.AppNotification

class MusicService: Service() {
    private val myBinder = MyBinder()
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }
    private val appNotification: AppNotification by lazy { AppNotification(applicationContext) }
    var playTag = 0
    lateinit var mSongList: MutableList<Song>
    var currentIndex = -1
    private val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.action){
                AppNotification.ACTION_NEXT -> nextSong()
                AppNotification.ACTION_PAUSE -> playPause()
                AppNotification.ACTION_PREVIOUS -> previousSong()
                AppNotification.ACTION_SEEK -> {
                    val position = intent.extras?.getLong("position") ?: getCurrentPosition()
                    mediaSeekTo(position.toInt())
                }
            }
        }

    }

    override fun onBind(p0: Intent?): IBinder {
        return myBinder
    }

    override fun onCreate() {
        mediaPlayer.setOnCompletionListener {
            when (playTag){
                0 -> nextSong()
                1 -> repeatOne()
                2 -> shuffle()
            }
        }
        val filter = IntentFilter()
        filter.addAction(AppNotification.ACTION_PREVIOUS)
        filter.addAction(AppNotification.ACTION_PAUSE)
        filter.addAction(AppNotification.ACTION_NEXT)
        filter.addAction(AppNotification.ACTION_SEEK)
        applicationContext.registerReceiver(broadcastReceiver, filter)
    }

    fun isPlaying() = mediaPlayer.isPlaying

    fun getCurrentPosition() = mediaPlayer.currentPosition

    fun getDuration() = mediaPlayer.duration

    fun mediaSeekTo(process: Int) {
        mediaPlayer.seekTo(process)
        appNotification.updatePlayBackState(process.toLong())
        if (!isPlaying()){
            appNotification.pauseSeekBar()
        }
    }

    fun setSongList(list: MutableList<Song>){
        mSongList = list
    }

    fun playSong(){
        mediaPlayer.reset()
        mediaPlayer.setDataSource(mSongList[currentIndex].path)
        mediaPlayer.prepare()
        mediaPlayer.start()
        callChangeData(R.drawable.ic_pause, mSongList[currentIndex], true)
        appNotification.createNotification(mSongList[currentIndex], R.drawable.ic_pause
            , mediaPlayer.duration.toLong(), mediaPlayer.currentPosition.toLong())
    }

    fun nextSong(){
        currentIndex = (currentIndex + 1) % mSongList.size
        playSong()
    }

    fun previousSong(){
        if (currentIndex > 0){
            currentIndex -= 1
        }else{
            currentIndex = mSongList.size - 1
        }
        playSong()
    }

    fun playPause(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
            appNotification.createNotification(mSongList[currentIndex], R.drawable.ic_play
                , mediaPlayer.duration.toLong(), mediaPlayer.currentPosition.toLong())
            appNotification.pauseSeekBar()
            callChangeData(R.drawable.ic_play, mSongList[currentIndex], false)
        }else {
            mediaPlayer.start()
            appNotification.createNotification(mSongList[currentIndex], R.drawable.ic_pause
                , mediaPlayer.duration.toLong(), mediaPlayer.currentPosition.toLong())
            callChangeData(R.drawable.ic_pause, mSongList[currentIndex], false)
        }
    }

    fun addCallBack(change: (Int, Song, Boolean) -> Unit){
        changeData.add { img, song, isChangeImg -> change(img, song, isChangeImg) }
    }

    private fun callChangeData(img: Int, song: Song, isChangeImg: Boolean){
        changeData.forEach { it(img, song, isChangeImg) }
    }

    private fun shuffle() {
        val size = mSongList.size - 1
        currentIndex = (0..size).random()
        playSong()
    }

    private fun repeatOne() {
        playSong()
    }

    companion object{
        private var changeData = mutableListOf<(Int, Song, Boolean) -> Unit>()
    }

    inner class MyBinder: Binder(){
        fun getService(): MusicService{
            return this@MusicService
        }
    }
}
