package com.example.musicapp.dataSource

import android.content.Context
import com.example.musicapp.dataSource.model.Song

interface SongDataSource {
    fun getSongList(context: Context, listener: OnResultListener<MutableList<Song>>)
}
