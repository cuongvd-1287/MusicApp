package com.example.musicapp

import android.media.MediaPlayer
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.musicapp.dataSource.model.Song

open class BaseFragment: Fragment() {

    fun addListener(previous: ImageView, playPause: ImageView, next: ImageView, change: (Int, Song) -> Unit){
        playPause.setOnClickListener {
            playPause()
            if (mediaPlayer.isPlaying) {
                change(R.drawable.ic_pause, songList[currentIndex])
                playPause.setImageResource(R.drawable.ic_pause)
            } else {
                change(R.drawable.ic_play, songList[currentIndex])
                playPause.setImageResource(R.drawable.ic_play)
            }
        }
        next.setOnClickListener {
            nextSong()
            change(R.drawable.ic_pause, songList[currentIndex])
            playPause.setImageResource(R.drawable.ic_pause)
        }
        previous.setOnClickListener {
            previousSong()
            change(R.drawable.ic_pause, songList[currentIndex])
            playPause.setImageResource(R.drawable.ic_pause)
        }
    }

    companion object{
        lateinit var songList: MutableList<Song>
        var mediaPlayer: MediaPlayer = MediaPlayer()
        var currentIndex = 0

        fun playSong(){
            mediaPlayer.reset()
            mediaPlayer.setDataSource(songList[currentIndex].path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

        fun nextSong(){
            currentIndex = (currentIndex + 1) % songList.size
            playSong()
        }

        fun previousSong(){
            if (currentIndex > 0){
                currentIndex -= 1
            }else{
                currentIndex = songList.size - 1
            }
            playSong()
        }

        fun playPause(){
            if (mediaPlayer.isPlaying){
                mediaPlayer.pause()
            }else {
                mediaPlayer.start()
            }
        }
    }
}