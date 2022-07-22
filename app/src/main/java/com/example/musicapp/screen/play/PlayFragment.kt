package com.example.musicapp.screen.play

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
import com.example.musicapp.BaseFragment
import com.example.musicapp.R
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.databinding.PlayFragmentBinding
import java.util.concurrent.TimeUnit

class PlayFragment(changeData: (Int, Song) -> Unit): BaseFragment() {

    private lateinit var binding: PlayFragmentBinding
    private lateinit var playPause: ImageView
    private lateinit var next: ImageView
    private lateinit var previous: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var currentProgress: TextView
    private lateinit var totalTime: TextView
    private lateinit var back: ImageView
    private lateinit var title: TextView
    private lateinit var subTitle: TextView
    private var changeData: (Int, Song) -> Unit

    init {
        this.changeData = changeData
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PlayFragmentBinding.inflate(layoutInflater)
        playPause = binding.playIc
        next = binding.nextIc
        previous = binding.previousIc
        seekBar = binding.seekBar
        currentProgress = binding.currentProgress
        totalTime = binding.totalProgress
        back = binding.keyDown
        title = binding.titleSong
        subTitle = binding.subTitleSong
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mediaPlayer.setOnCompletionListener {
            nextSong()
            val song = songList[currentIndex]
            title.text = song.title
            subTitle.text = song.artist
            changeData(R.drawable.ic_pause, song)
        }
        if (!mediaPlayer.isPlaying){
            playPause.setImageResource(R.drawable.ic_play)
        }
        updateData()
        val handler = Handler(Looper.getMainLooper())
        val runnable = object: Runnable {
            override fun run() {
                val curPosition = mediaPlayer.currentPosition
                seekBar.progress = curPosition
                currentProgress.text = getTimeFormat(curPosition.toLong())
                handler.post(this)
            }
        }
        handler.post(runnable)
        super.onViewCreated(view, savedInstanceState)
        addListener()
    }

    private fun addListener(){
        super.addListener(previous, playPause, next){
            img, song ->
            updateData()
            title.text = song.title
            subTitle.text = song.artist
            changeData(img, song)
        }
        back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        seekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, currentProgress: Int, fromUser: Boolean) {
                if (fromUser){
                    mediaPlayer.seekTo(currentProgress)
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

    private fun updateData(){
        title.text = songList[currentIndex].title
        subTitle.text = songList[currentIndex].artist
        val duration = mediaPlayer.duration
        seekBar.max = duration
        totalTime.text = getTimeFormat(duration.toLong())
    }
}