package com.example.musicapp.screen.listSong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.musicapp.BaseFragment
import com.example.musicapp.R
import com.example.musicapp.dataSource.SongDataSource
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.databinding.FragmentSongListBinding
import com.example.musicapp.screen.SongPresenter
import com.example.musicapp.screen.listSong.adapter.SongAdapter
import com.example.musicapp.screen.play.PlayFragment

class SongListFragment : BaseFragment(), SongsContract.View, AdapterView.OnItemClickListener {

    private lateinit var songPresenter: SongPresenter
    private lateinit var binding: FragmentSongListBinding
    private lateinit var listView: ListView
    private lateinit var playingSide: ConstraintLayout
    private lateinit var titlePlay: TextView
    private lateinit var subTitlePlay: TextView
    private lateinit var subPrevious: ImageView
    private lateinit var subPlay: ImageView
    private lateinit var subNext: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongListBinding.inflate(layoutInflater)
        listView = binding.listSong
        playingSide = binding.subSide
        titlePlay = binding.titlePlay
        subTitlePlay = binding.subTitlePlay
        subPrevious = binding.subPrevious
        subPlay = binding.subPlay
        subNext = binding.subNext

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        songPresenter = SongPresenter(SongDataSource.getInstance(requireActivity().applicationContext))
        setSong(songPresenter.getSong())
        addListener(subPrevious, subPlay, subNext){ _, song -> changeText(song) }
        playingSide.setOnClickListener { addPlayFragment() }
    }

    override fun setSong(list: MutableList<Song>) {
        songList = list
        listView.adapter = context?.let { SongAdapter(it,list) }
        listView.onItemClickListener = this
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        playingSide.visibility = View.VISIBLE
        currentIndex = p2
        titlePlay.text = songList[currentIndex].title
        subTitlePlay.text = songList[currentIndex].artist
        playSong()
        addPlayFragment()
    }

    private fun addPlayFragment(){
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .add(R.id.layoutContainer, PlayFragment{ img, song -> changeData(img, song) })
            .addToBackStack(null)
            .commit()
    }

    private fun changeData(img: Int, song: Song){
        changeText(song)
        subPlay.setImageResource(img)
    }

    private fun changeText(song: Song){
        titlePlay.text = song.title
        subTitlePlay.text = song.artist
    }
}
