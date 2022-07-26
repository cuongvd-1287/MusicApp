package com.example.musicapp.screen.listSong.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
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
        val view: View = inflater!!.inflate(R.layout.song_item, parent, false)
        val song: Song = songList[position]
        val title: TextView = view.findViewById(R.id.title)
        val artist: TextView = view.findViewById(R.id.artist)
        val img: ImageView = view.findViewById(R.id.img)
        title.text = song.title
        artist.text = song.artist
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(song.path)
        img.let {
            Glide.with(context)
                .load(mmr.embeddedPicture)
                .placeholder(R.drawable.ic_baseline_music_note)
                .into(it)
        }
        return view
    }

    override fun getCount(): Int {
        return songList.size
    }
}
