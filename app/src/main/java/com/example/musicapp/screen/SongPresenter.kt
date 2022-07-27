package com.example.musicapp.screen

import android.content.Context
import com.example.musicapp.dataSource.OnResultListener
import com.example.musicapp.dataSource.SongRepository
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.screen.listSong.SongsContract

class SongPresenter(private val songRepo: SongRepository): SongsContract.Presenter {

    private var view: SongsContract.View? = null

    override fun getSong(context: Context) {
        songRepo.getSongList(context, object: OnResultListener<MutableList<Song>>{
            override fun onSuccess(data: MutableList<Song>) {
                view?.onGetSongCompleted(data)
            }
        })
    }

    fun setView(view: SongsContract.View?){
        this.view = view
    }
}
