package com.symbol.game.ui.other

import android.media.MediaPlayer
import android.media.ToneGenerator.MAX_VOLUME
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.symbol.game.R
import com.symbol.game.domain.AppPrefs
import com.symbol.game.domain.MusicController

class MainActivity : AppCompatActivity(), MusicController {
    private val viewModel: ActivityViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer
    private val sharedPrefs by lazy {
        AppPrefs(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        mediaPlayer.isLooping = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SEC", mediaPlayer.currentPosition)
    }

    override fun onResume() {
        super.onResume()
        startMusic()
    }

    override fun onPause() {
        super.onPause()
        pauseMusic()
    }

    override fun startMusic() {
        val volume =  (1 - (Math.log(MAX_VOLUME - sharedPrefs.getVolume().toDouble()) / Math.log(MAX_VOLUME.toDouble())));
        mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
        mediaPlayer.seekTo(viewModel.currentSec)
        mediaPlayer.start()
    }

    override fun pauseMusic() {
        try {
            viewModel.currentSec = mediaPlayer.currentPosition
            mediaPlayer.pause()
        } catch (_: Throwable) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}