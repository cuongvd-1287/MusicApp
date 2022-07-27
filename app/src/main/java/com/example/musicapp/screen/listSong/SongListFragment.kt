package com.example.musicapp.screen.listSong

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.musicapp.BaseFragment
import com.example.musicapp.R
import com.example.musicapp.constant.contentIntentName
import com.example.musicapp.dataSource.SongLocalDataSource
import com.example.musicapp.dataSource.SongRepository
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.databinding.FragmentSongListBinding
import com.example.musicapp.screen.MainActivity
import com.example.musicapp.screen.SongPresenter
import com.example.musicapp.screen.listSong.adapter.SongAdapter
import com.example.musicapp.screen.play.PlayFragment

class SongListFragment : BaseFragment(), SongsContract.View, AdapterView.OnItemClickListener {

    private val songPresenter: SongPresenter by lazy {
        SongPresenter(
            SongRepository
                .getInstance(SongLocalDataSource.getInstance())
        )
    }

    private lateinit var binding: FragmentSongListBinding
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
        songPresenter.setView(this)
        val playFragment = activity?.intent?.getBooleanExtra(contentIntentName, false)
        if (playFragment == true) {
            addPlayFragment()
        }
        addListener()
    }

    override fun initData() {
        activity?.let {
            songPresenter.getSong(it.applicationContext)
            binding.listSong.onItemClickListener = this
        }
        val callBack: (Int, Song, Boolean) -> Unit = { img, song, isChangeImg ->
            binding.subPlay.setImageResource(img)
            binding.titlePlay.text = song.title
            binding.subTitlePlay.text = song.artist
            if (isChangeImg) {
                loadImg(song)
            }
        }
        if (mActivity.isServiceBound) {
            mActivity.musicService?.addCallBack(callBack)
        } else {
            dataPasser.passCallBack(callBack)
        }
    }

    private fun setSong(list: MutableList<Song>) {
        binding.listSong.adapter = context?.let { SongAdapter(it, list) }
        if (mActivity.isServiceBound) {
            mActivity.musicService?.setSongList(list)
        } else {
            dataPasser.passData(list)
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (mActivity.isServiceBound) {
            mActivity.musicService?.currentIndex = p2
            mActivity.musicService?.playSong()
            binding.subSide.visibility = View.VISIBLE
            addPlayFragment()
        }
    }

    private fun addPlayFragment() {
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

    private fun loadImg(song: Song) {
        context?.let {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(song.path)
            Glide.with(it)
                .load(mmr.embeddedPicture)
                .placeholder(R.drawable.ic_baseline_music_note)
                .error(R.drawable.ic_baseline_music_note)
                .into(binding.imgMusicNote)
        }
    }

    private fun addListener() {
        binding.subNext.setOnClickListener {
            if (mActivity.isServiceBound) {
                mActivity.musicService?.nextSong()
            }
        }

        binding.subPrevious.setOnClickListener {
            if (mActivity.isServiceBound) {
                mActivity.musicService?.previousSong()
            }
        }

        binding.subPlay.setOnClickListener {
            if (mActivity.isServiceBound) {
                mActivity.musicService?.playPause()
            }
        }

        binding.subSide.setOnClickListener {
            if (mActivity.isServiceBound) {
                addPlayFragment()
            }
        }
    }

    override fun onGetSongCompleted(list: MutableList<Song>) {
        setSong(list)
    }

    interface OnDataPass {
        fun passData(list: MutableList<Song>)
        fun passCallBack(callBack: (Int, Song, Boolean) -> Unit)
    }
}
