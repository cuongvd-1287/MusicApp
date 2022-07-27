package com.example.musicapp.screen.play

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.bumptech.glide.Glide
import com.example.musicapp.BaseFragment
import com.example.musicapp.R
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.databinding.PlayFragmentBinding
import com.example.musicapp.screen.MainActivity
import java.util.concurrent.TimeUnit

class PlayFragment: BaseFragment() {

    private lateinit var binding: PlayFragmentBinding
    private val mActivity: MainActivity by lazy { activity as MainActivity }
    private val dataPasser: OnDataPass by lazy { context as OnDataPass }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PlayFragmentBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun initView() {
        if (mActivity.isServiceBound){
            val index = mActivity.musicService?.currentIndex
            val mSong = index?.let { mActivity.musicService?.mSongList?.get(it) }
            mSong?.let {
                updateData(it)
                loadImg(it)
            }
            when (mActivity.musicService?.playTag){
                1 -> binding.playMode.setImageResource(R.drawable.ic_repeat_one)
                2 -> binding.playMode.setImageResource(R.drawable.ic_shuffle)
            }
            mActivity.musicService?.isPlaying()?.let {
                if (!it){
                    binding.playIc.setImageResource(R.drawable.ic_play)
                }
            }
            val handler = Handler(Looper.getMainLooper())
            val runnable = object: Runnable {
                override fun run() {
                    val curPosition = mActivity.musicService?.getCurrentPosition() ?: 0
                    binding.seekBar.progress = curPosition
                    binding.currentProgress.text = getTimeFormat(curPosition.toLong())
                    handler.post(this)
                }
            }
            handler.post(runnable)
        }
    }

    override fun initData() {
        val callBack: (Int, Song, Boolean) -> Unit = {
            img, song, isChangeImg ->
            updateData(song)
            if (isChangeImg){
                loadImg(song)
            }
            binding.playIc.setImageResource(img)
        }
        if (mActivity.isServiceBound){
            mActivity.musicService?.addCallBack(callBack)
        }else {
            dataPasser.passCallBack(callBack)
        }
        addListener()
    }

    private fun addListener(){
        binding.nextIc.setOnClickListener {
            if (mActivity.isServiceBound){
                mActivity.musicService?.nextSong()
            }
        }

        binding.playIc.setOnClickListener {
            if (mActivity.isServiceBound){
                mActivity.musicService?.playPause()
            }
        }

        binding.previousIc.setOnClickListener {
            if (mActivity.isServiceBound){
                mActivity.musicService?.previousSong()
            }
        }

        binding.keyDown.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.playMode.setOnClickListener {
            if (mActivity.isServiceBound){
                var playTag = mActivity.musicService?.playTag ?: 0
                when (mActivity.musicService?.playTag){
                    0 -> binding.playMode.setImageResource(R.drawable.ic_repeat_one)
                    1 -> binding.playMode.setImageResource(R.drawable.ic_shuffle)
                    2 -> binding.playMode.setImageResource(R.drawable.ic_repeat)
                }
                playTag = (playTag + 1) % 3
                mActivity.musicService?.playTag = playTag
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, currentProgress: Int, fromUser: Boolean) {
                if (fromUser){
                    if (mActivity.isServiceBound){
                        mActivity.musicService?.mediaSeekTo(currentProgress)
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    fun getTimeFormat(time: Long): String{
        val hour = TimeUnit.MILLISECONDS.toHours(time)
        val minute = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(hour)
        val second = TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        var timeFormat = ""
        if (hour > 0){
            timeFormat = "$hour:"
        }
        timeFormat += "$minute:"
        timeFormat += if (second < 10) "0$second" else "$second"
        return timeFormat
    }

    private fun updateData(song: Song){
        binding.titleSong.text = song.title
        binding.subTitleSong.text = song.artist
        val duration = mActivity.musicService?.getDuration() ?: 0
        binding.seekBar.max = duration
        binding.totalProgress.text = getTimeFormat(duration.toLong())
    }

    private fun loadImg(song: Song){
        context?.let {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(song.path)
            Glide.with(it)
                .load(mmr.embeddedPicture)
                .placeholder(R.drawable.ic_baseline_music_note)
                .error(R.drawable.ic_baseline_music_note)
                .into(binding.imgMusic)
        }
    }

    interface OnDataPass{
        fun passCallBack(callBack: (Int, Song, Boolean) -> Unit)
    }
}
