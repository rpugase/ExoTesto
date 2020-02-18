package com.hridin.exotesto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), Player.EventListener {

    companion object {
        private const val url =
            "https://streamer.vip.ministra.com/eurosport-buydrm/index.mpd"
//            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"

        private const val licenseUrl = "https://wv-keyos.licensekeyserver.com/"
        private const val token =
            "PEtleU9TQXV0aGVudGljYXRpb25YTUw+CjxEYXRhPgogIDxHZW5lcmF0aW9uVGltZT4yMDIwLTAxLTIzIDEwOjAyOjE2Ljk0NDwvR2VuZXJhdGlvblRpbWU+CiAgPEV4cGlyYXRpb25UaW1lPjIwMjAtMDItMjIgMTA6MDI6MTYuOTQ2PC9FeHBpcmF0aW9uVGltZT4KICA8VW5pcXVlSWQ+NDg3ZjdiMjJmNjgzMTJkMmMxYmJjOTNiMWFlYTQ0NWI8L1VuaXF1ZUlkPgogIDxSU0FQdWJLZXlJZD5jYTMzZWU5YmQwOGI4YTZiMjIxYjcwMzA3NDA4OTZlMDwvUlNBUHViS2V5SWQ+CiAgPFdpZGV2aW5lUG9saWN5IGZsX0NhblBsYXk9InRydWUiIGZsX0NhblBlcnNpc3Q9ImZhbHNlIiAvPgogIDxXaWRldmluZUNvbnRlbnRLZXlTcGVjIFRyYWNrVHlwZT0iSEQiPgogICAgPFNlY3VyaXR5TGV2ZWw+MzwvU2VjdXJpdHlMZXZlbD4KICA8L1dpZGV2aW5lQ29udGVudEtleVNwZWM+CjwvRGF0YT4KPFNpZ25hdHVyZT5oNW44L0NZKzMyU3pUMWdMdGkwaHdueVl5bys3M1pzcUFrTS9rUmlENzEwTitoRHdlZWNaL1VOVnhQWngzQlVyYWdFbXU1eDZDTDRXVzRDcyttNmV1ZytuRjBhVVZnVW5lYXpyZXFxekJYS1NTM2dJb1NZMFllOSt1SVM2ZVpveHFPVXlxS3ZSZVhWTUwrc09DOXBqUjRZK2NidVdLMHFEb3pRWkpqWXRBN0JLMytidDV0MytkYUtVRnpqblhvV3lkdkRUR09XMXFIdVBheUl0RGNDNVo0Q3BNSXdlSyt6NVp0aTJRZVowaEtTY0MxWXRwOHdVaEZyYWhyOXphaHd0Zmd6bVdvbzFOdWxFbXVjZWhWbTdaRzYwMlhnMDRKSkozbDh5TnFrelBaTXJxVGwxbUtMS285WHRpV2RmZDNUOFBLYlpOdTF2OUpKV0xvWVliY1hCQ0E9PTwvU2lnbmF0dXJlPgo8L0tleU9TQXV0aGVudGljYXRpb25YTUw+"
    }

    private val exoPlayer: SimpleExoPlayer by lazy { initExoPlayer() }
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
    }

    private fun initExoPlayer(): SimpleExoPlayer {
        val rendersFactory = DefaultRenderersFactory(this)
        val trackSelector = DefaultTrackSelector()

        val drmSessionManager = buildDrmSessionManager(licenseUrl, hashMapOf("customdata" to token))

        return ExoPlayerFactory.newSimpleInstance(this, rendersFactory, trackSelector, drmSessionManager).also {
            it.playWhenReady = true
        }
    }

    private fun buildDrmSessionManager(licenseUrl: String, hashMap: HashMap<String, String>): DefaultDrmSessionManager<FrameworkMediaCrypto> {
        val mediaDrmCallback = HttpMediaDrmCallback(licenseUrl, dataSourceFactory)

        for (property in hashMap.entries) {
            mediaDrmCallback.setKeyRequestProperty(property.key, property.value)
        }

        return DefaultDrmSessionManager.newWidevineInstance(mediaDrmCallback, null)
            .apply { setMode(DefaultDrmSessionManager.MODE_PLAYBACK, null) }

    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        super.onPlayerError(error)

        Timber.d(error)
    }

    override fun onDestroy() {
        exoPlayer.release()
        super.onDestroy()
    }
}
