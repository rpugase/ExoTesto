package com.hridin.exotesto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val url =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    private val exoPlayer: SimpleExoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(this) }
    private val dataSourceFactory: DefaultHttpDataSourceFactory by lazy {
        DefaultHttpDataSourceFactory(
            Util.getUserAgent(this, getString(R.string.app_name))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPlayer.player = exoPlayer

        exoPlayer.prepare(MediaSourceFactoryImpl.create(url, dataSourceFactory))

        exoPlayer.playWhenReady = true
    }

    override fun onDestroy() {
        exoPlayer.release()
        super.onDestroy()
    }
}
