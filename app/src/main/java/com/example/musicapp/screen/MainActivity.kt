package com.example.musicapp.screen

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.screen.listSong.SongListFragment
import com.example.musicapp.screen.play.PlayFragment
import com.example.musicapp.service.MusicService

class MainActivity : AppCompatActivity(), SongListFragment.OnDataPass, PlayFragment.OnDataPass {

    private lateinit var binding: ActivityMainBinding
    private lateinit var songList: MutableList<Song>
    private lateinit var callBack: (Int, Song, Boolean) -> Unit
    lateinit var musicService: MusicService
    var serviceBound = false
    private var myConnection = object: ServiceConnection{

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val myBinder = binder as MusicService.MyBinder
            musicService = myBinder.getService()
            serviceBound = true
            if (this@MainActivity::songList.isInitialized){
                musicService.setSongList(songList)
                musicService.addCallBack(callBack)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MusicService::class.java)
        applicationContext.bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        grantPermission()
    }

    private fun grantPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }else{
                addFragment()
            }
        } else{
            addFragment()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100){
            if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED ){
                addFragment()
            }
        }
    }

    private fun addFragment(){
        supportFragmentManager.beginTransaction()
            .replace(binding.layoutContainer.id, SongListFragment())
            .commit()
    }

    override fun passData(list: MutableList<Song>) {
        songList = list
    }

    override fun passCallBack(callBack: (Int, Song, Boolean) -> Unit) {
        this.callBack = callBack
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationContext.unbindService(myConnection)
    }
}
