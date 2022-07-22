package com.example.musicapp.screen.listSong.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.musicapp.R
import com.example.musicapp.dataSource.model.Song

class SongAdapter(context: Context, songList: MutableList<Song>)
    : ArrayAdapter<Song>(context, R.layout.song_item, songList){
    private var mcontext: Context
    private var songList: MutableList<Song>

    init {
        this.mcontext = context
        this.songList = songList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater? = LayoutInflater.from(context)
        val view = inflater!!.inflate(R.layout.song_item, parent, false)
        val song: Song = songList[position]
        val title: TextView? = view?.findViewById(R.id.title)
        val artist: TextView? = view?.findViewById(R.id.artist)
        title?.text = song.title
        artist?.text = song.artist
        return view
    }

    override fun getCount(): Int {
        return songList.size
    }
}