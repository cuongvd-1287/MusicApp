package com.example.musicapp.screen.play

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.musicapp.BaseFragment
import com.example.musicapp.R
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.databinding.PlayFragmentBinding
import com.example.musicapp.screen.MainActivity
import java.util.concurrent.TimeUnit

class PlayFragment: BaseFragment() {

    private lateinit var binding: PlayFragmentBinding
    private val playPause: ImageView by lazy { binding.playIc }
    private val next: ImageView by lazy { binding.nextIc }
    private val previous: ImageView by lazy { binding.previousIc }
    private val playMode: ImageView by lazy { binding.playMode }
    private val mseekBar: SeekBar by lazy { binding.seekBar }
    private val currentProgress: TextView by lazy { binding.currentProgress }
    private val totalTime: TextView by lazy { binding.totalProgress }
    private val back: ImageView by lazy { binding.keyDown }
    private val title: TextView by lazy { binding.titleSong }
    private val subTitle: TextView by lazy { binding.subTitleSong }
    private val imgMusic: ImageView by lazy { binding.imgMusic }
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
        if (mActivity.serviceBound){
            val index = mActivity.musicService.currentIndex
            val mSong = mActivity.musicService.mSongList[index]
            updateData(mSong)
            loadImg(mSong)
            when (mActivity.musicService.playTag){
                1 -> playMode.setImageResource(R.drawable.ic_repeat_one)
                2 -> playMode.setImageResource(R.drawable.ic_shuffle)
            }
            if (!mActivity.musicService.isPlaying()){
                playPause.setImageResource(R.drawable.ic_play)
            }
            val handler = Handler(Looper.getMainLooper())
            val runnable = object: Runnable {
                override fun run() {
                    val curPosition = mActivity.musicService.getCurrentPosition()
                    mseekBar.progress = curPosition
                    currentProgress.text = getTimeFormat(curPosition.toLong())
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
            playPause.setImageResource(img)
        }
        if (mActivity.serviceBound){
            mActivity.musicService.addCallBack(callBack)
        }else {
            dataPasser.passCallBack(callBack)
        }
        addListener()
    }

    private fun addListener(){
        next.setOnClickListener {
            if (mActivity.serviceBound){
                mActivity.musicService.nextSong()
            }
        }

        playPause.setOnClickListener {
            if (mActivity.serviceBound){
                mActivity.musicService.playPause()
            }
        }

        previous.setOnClickListener {
            if (mActivity.serviceBound){
                mActivity.musicService.previousSong()
            }
        }

        back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        playMode.setOnClickListener {
            if (mActivity.serviceBound){
                var playTag = mActivity.musicService.playTag
                when (mActivity.musicService.playTag){
                    0 -> playMode.setImageResource(R.drawable.ic_repeat_one)
                    1 -> playMode.setImageResource(R.drawable.ic_shuffle)
                    2 -> playMode.setImageResource(R.drawable.ic_repeat)
                }
                playTag = (playTag + 1) % 3
                mActivity.musicService.playTag = playTag
            }
        }

        mseekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, currentProgress: Int, fromUser: Boolean) {
                if (fromUser){
                    if (mActivity.serviceBound){
                        mActivity.musicService.mediaSeekTo(currentProgress)
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
        title.text = song.title
        subTitle.text = song.artist
        val duration = mActivity.musicService.getDuration()
        mseekBar.max = duration
        totalTime.text = getTimeFormat(duration.toLong())
    }

    private fun loadImg(song: Song){
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(song.path)
        Glide.with(mActivity.applicationContext)
            .load(mmr.embeddedPicture)
            .placeholder(R.drawable.ic_baseline_music_note)
            .into(imgMusic)
    }

    interface OnDataPass{
        fun passCallBack(callBack: (Int, Song, Boolean) -> Unit)
    }
}
