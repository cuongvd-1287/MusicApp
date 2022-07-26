package com.example.musicapp.screen

import android.content.Context
import com.example.musicapp.dataSource.SongRepository
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.screen.listSong.SongsContract

class SongPresenter(private val songRepo: SongRepository): SongsContract.Presenter {

    override fun getSong(context: Context): MutableList<Song> {
        return songRepo.getSongList(context)
    }
}
