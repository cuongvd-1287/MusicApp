package com.example.musicapp.dataSource

import android.content.Context
import android.provider.MediaStore
import com.example.musicapp.dataSource.model.Song
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class SongLocalDataSource {

    fun getSong(context: Context): MutableList<Song> {
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA)
        val list: MutableList<Song> = mutableListOf()
        val cursor = contentResolver.query(uri, projection,
            MediaStore.Audio.Media.IS_MUSIC + " != 0", null,
            MediaStore.Audio.Media.TITLE)
        val threadPoolExecutor = ThreadPoolExecutor(1, 1, 60
            , TimeUnit.SECONDS, LinkedBlockingQueue())
        threadPoolExecutor.execute {
            cursor?.let {
                val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                if (cursor.moveToFirst()) {
                    do {
                        val song = Song(
                            cursor.getString(title), cursor.getString(artist),
                            cursor.getString(path)
                        )
                        list.add(song)
                    } while (cursor.moveToNext())
                }
            }
            cursor?.close()
        }
        return list
    }

    companion object{
        private var instance: SongLocalDataSource? = null

        fun getInstance(): SongLocalDataSource {
            return synchronized(this){
                instance ?: SongLocalDataSource().also { instance = it }
            }
        }
    }
}
