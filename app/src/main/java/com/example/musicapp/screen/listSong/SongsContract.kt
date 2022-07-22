package com.example.musicapp.screen.listSong

import com.example.musicapp.dataSource.model.Song

class SongsContract {
    interface View {
        fun setSong(list: MutableList<Song>)
    }

    interface Presenter{
        fun getSong(): MutableList<Song>
    }
}