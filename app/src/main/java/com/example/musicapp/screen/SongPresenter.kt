package com.example.musicapp.screen

import android.content.Context
import com.example.musicapp.dataSource.SongRepository
import com.example.musicapp.screen.listSong.SongsContract

class SongPresenter(private val songRepo: SongRepository): SongsContract.Presenter {

    private var view: SongsContract.View? = null

    override fun getSong(context: Context) {
        view?.onGetSongCompleted(songRepo.getSongList(context))
    }

    fun setView(view: SongsContract.View?){
        this.view = view
    }
}
