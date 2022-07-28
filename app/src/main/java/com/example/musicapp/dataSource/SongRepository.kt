package com.example.musicapp.dataSource

import android.content.Context
import com.example.musicapp.dataSource.model.Song

class SongRepository(private val source: SongLocalDataSource): SongDataSource {
    override fun getSongList(context: Context, listener: OnResultListener<MutableList<Song>>) {
        source.getSong(context, listener)
    }

    companion object {
        private var instance: SongRepository? = null

        fun getInstance(localSource: SongLocalDataSource) =
            synchronized(this){
                instance ?: SongRepository(localSource).also { instance = it }
            }
    }
}
