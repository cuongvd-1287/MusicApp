package com.example.musicapp.screen.listSong

import android.content.Context
import com.example.musicapp.dataSource.model.Song

class SongsContract {
    interface View {
        fun onGetSongCompleted(list: MutableList<Song>)
    }

    interface Presenter{
        fun getSong(context: Context)
    }
}
