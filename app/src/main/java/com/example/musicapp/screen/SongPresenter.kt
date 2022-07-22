package com.example.musicapp.screen

import com.example.musicapp.dataSource.SongDataSource
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.screen.listSong.SongsContract

class SongPresenter(private val songData: SongDataSource): SongsContract.Presenter {

    override fun getSong(): MutableList<Song> {
        return songData.getSong()
    }

}