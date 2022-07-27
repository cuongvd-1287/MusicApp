package com.example.musicapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.musicapp.R
import com.example.musicapp.constant.*
import com.example.musicapp.dataSource.model.Song
import com.example.musicapp.screen.MainActivity

class AppNotification(context: Context) {
    private val context: Context
    private val channelId = "1000"
    private val notificationId = 1100
    private val requestCode = 0
    private val notificationManagerCompat: NotificationManagerCompat
    private val mediaSession: MediaSessionCompat
    private val contentPending: PendingIntent
    private val previousPending: PendingIntent
    private val pausePending: PendingIntent
    private val nextPending: PendingIntent
    private var seekPending: PendingIntent? = null

    init {
        this.context = context
        this.mediaSession = MediaSessionCompat(context, mediaSessionTag)
        createChannel()
        this.notificationManagerCompat = NotificationManagerCompat.from(context)
        val content = Intent(context, MainActivity::class.java)
        content.putExtra(contentIntentName, true)
        this.contentPending = PendingIntent.getActivity(context, requestCode, content
            , PendingIntent.FLAG_CANCEL_CURRENT)

        previousPending = PendingIntent.getBroadcast(context, requestCode
            , Intent(ACTION_PREVIOUS)
            , PendingIntent.FLAG_UPDATE_CURRENT)
        pausePending = PendingIntent.getBroadcast(context, requestCode, Intent(ACTION_PAUSE)
            , PendingIntent.FLAG_UPDATE_CURRENT)
        nextPending = PendingIntent.getBroadcast(context, requestCode, Intent(ACTION_NEXT)
            , PendingIntent.FLAG_UPDATE_CURRENT)

        this.mediaSession.setCallback(object :MediaSessionCompat.Callback(){
            override fun onSeekTo(pos: Long) {
                val intentSeek = Intent(ACTION_SEEK)
                intentSeek.putExtra(seekIntentName, pos)
                seekPending = PendingIntent.getBroadcast(context, requestCode, intentSeek
                    , PendingIntent.FLAG_UPDATE_CURRENT)
                seekPending?.send()
                updatePlayBackState(pos)
            }
        })
    }
    fun createNotification(song: Song, playPauseImg: Int, duration: Long, position: Long){
        val metadataBuilder = MediaMetadataCompat.Builder()
        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, position, 1F)
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
            .build())
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, song.title)
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, song.artist)
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, duration)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(song.path)
        Glide.with(context)
            .asBitmap()
            .load(mmr.embeddedPicture)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, resource)
                    mediaSession.setMetadata(metadataBuilder.build())
                    createNotification(playPauseImg)
                }


                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    mediaSession.setMetadata(metadataBuilder.build())
                    createNotification(playPauseImg)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun createNotification(playPauseImg: Int){
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(contentPending)
            .addAction(R.drawable.ic_previous, actionPreviousTitle, previousPending)
            .addAction(playPauseImg, actionPauseTitle, pausePending)
            .addAction(R.drawable.ic_next, actionNextTitle, nextPending)
            .setStyle( MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        notificationManagerCompat.notify(notificationId, builder)
    }

    private fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            channel.description = channelDescription
            val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updatePlayBackState(position: Long){
        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, position, 1F)
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
            .build())
    }

    fun pauseSeekBar(){
        val position = mediaSession.controller.playbackState.position
        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, position, 0F)
            .setActions(PlaybackStateCompat.ACTION_PAUSE)
            .build())
    }
}
