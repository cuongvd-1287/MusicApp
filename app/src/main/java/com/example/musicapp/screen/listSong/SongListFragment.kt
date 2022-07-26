package com.example.musicapp.screen.listSong

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.musicapp.BaseFragment
import com.example.musicapp.R
import com.example.musicapp.dataSource.SongLocalDataSource
import com.example.musicapp.dataSource.SongRepository
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.databinding.FragmentSongListBinding
import com.example.musicapp.screen.MainActivity
import com.example.musicapp.screen.SongPresenter
import com.example.musicapp.screen.listSong.adapter.SongAdapter
import com.example.musicapp.screen.play.PlayFragment

class SongListFragment : BaseFragment(), SongsContract.View, AdapterView.OnItemClickListener {

    private lateinit var songPresenter: SongPresenter
    private lateinit var binding: FragmentSongListBinding
    private val listView: ListView by lazy { binding.listSong }
    private val playingSide: ConstraintLayout by lazy { binding.subSide }
    private val titlePlay: TextView by lazy { binding.titlePlay }
    private val subTitlePlay: TextView by lazy { binding.subTitlePlay }
    private val subPrevious: ImageView by lazy { binding.subPrevious }
    private val subPlay: ImageView by lazy { binding.subPlay }
    private val subNext: ImageView by lazy { binding.subNext }
    private val subImg: ImageView by lazy { binding.imgMusicNote }
    private val dataPasser: OnDataPass by lazy { context as OnDataPass }
    private val mActivity: MainActivity by lazy { activity as MainActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        val playFragment = activity?.intent?.getBooleanExtra("playFragment", false)
        if (playFragment == true){
            addPlayFragment()
        }
        addListener()
    }

    override fun initData() {
        songPresenter = SongPresenter(SongRepository.getInstance(SongLocalDataSource.getInstance()))
        activity?.let {
            val list = songPresenter.getSong(it.applicationContext)
            setSong(list)
            listView.onItemClickListener = this
        }
        val callBack: (Int, Song, Boolean) -> Unit = {
                img, song, isChangeImg ->
                    subPlay.setImageResource(img)
                    titlePlay.text = song.title
                    subTitlePlay.text = song.artist
                    if (isChangeImg) {
                        loadImg(song)
                    }
        }
        if (mActivity.serviceBound){
            mActivity.musicService.addCallBack (callBack)
        }else{
            dataPasser.passCallBack(callBack)
        }
    }

    private fun setSong(list: MutableList<Song>) {
        listView.adapter = context?.let { SongAdapter(it, list) }
        if (mActivity.serviceBound){
            mActivity.musicService.setSongList(list)
        } else{
            dataPasser.passData(list)
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (mActivity.serviceBound){
            mActivity.musicService.currentIndex = p2
            mActivity.musicService.playSong()
            playingSide.visibility = View.VISIBLE
            addPlayFragment()
        }
    }

    private fun addPlayFragment(){
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .add(R.id.layoutContainer, PlayFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun loadImg(song: Song){
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(song.path)
        Glide.with(mActivity.applicationContext)
            .load(mmr.embeddedPicture)
            .placeholder(R.drawable.ic_baseline_music_note)
            .into(subImg)
    }

    private fun addListener(){
        subNext.setOnClickListener{
            if (mActivity.serviceBound){
                mActivity.musicService.nextSong()
            }
        }

        subPrevious.setOnClickListener{
            if (mActivity.serviceBound){
                mActivity.musicService.previousSong()
            }
        }

        subPlay.setOnClickListener{
            if (mActivity.serviceBound){
                mActivity.musicService.playPause()
            }
        }

        playingSide.setOnClickListener{
            if (mActivity.serviceBound){
                addPlayFragment()
            }
        }
    }

    interface OnDataPass{
        fun passData(list: MutableList<Song>)
        fun passCallBack(callBack: (Int, Song, Boolean) -> Unit)
    }
}
