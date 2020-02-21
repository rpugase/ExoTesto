package com.hridin.exotesto

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        private const val url =
//            "https://storage.googleapis.com/wvmedia/cbc1/h264/tears/tears_aes_cbc1_hd.mpd"
//            "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd"
            "https://streamer.vip.ministra.com/eurosport-buydrm/index.mpd"
//            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"

        private const val licenseUrl =
//            "https://proxy.uat.widevine.com/proxy?video_id=48fcc369939ac96c&provider=widevine_test"
//            "https://proxy.uat.widevine.com/proxy?video_id=d286538032258a1c&provider=widevine_test"
//            "http://10.110.11.105:8090/"
            "https://wv-keyos.licensekeyserver.com/"

        private const val token =
            "PEtleU9TQXV0aGVudGljYXRpb25YTUw+CjxEYXRhPgogIDxHZW5lcmF0aW9uVGltZT4yMDIwLTAxLTIzIDEwOjAyOjE2Ljk0NDwvR2VuZXJhdGlvblRpbWU+CiAgPEV4cGlyYXRpb25UaW1lPjIwMjAtMDItMjIgMTA6MDI6MTYuOTQ2PC9FeHBpcmF0aW9uVGltZT4KICA8VW5pcXVlSWQ+NDg3ZjdiMjJmNjgzMTJkMmMxYmJjOTNiMWFlYTQ0NWI8L1VuaXF1ZUlkPgogIDxSU0FQdWJLZXlJZD5jYTMzZWU5YmQwOGI4YTZiMjIxYjcwMzA3NDA4OTZlMDwvUlNBUHViS2V5SWQ+CiAgPFdpZGV2aW5lUG9saWN5IGZsX0NhblBsYXk9InRydWUiIGZsX0NhblBlcnNpc3Q9ImZhbHNlIiAvPgogIDxXaWRldmluZUNvbnRlbnRLZXlTcGVjIFRyYWNrVHlwZT0iSEQiPgogICAgPFNlY3VyaXR5TGV2ZWw+MzwvU2VjdXJpdHlMZXZlbD4KICA8L1dpZGV2aW5lQ29udGVudEtleVNwZWM+CjwvRGF0YT4KPFNpZ25hdHVyZT5oNW44L0NZKzMyU3pUMWdMdGkwaHdueVl5bys3M1pzcUFrTS9rUmlENzEwTitoRHdlZWNaL1VOVnhQWngzQlVyYWdFbXU1eDZDTDRXVzRDcyttNmV1ZytuRjBhVVZnVW5lYXpyZXFxekJYS1NTM2dJb1NZMFllOSt1SVM2ZVpveHFPVXlxS3ZSZVhWTUwrc09DOXBqUjRZK2NidVdLMHFEb3pRWkpqWXRBN0JLMytidDV0MytkYUtVRnpqblhvV3lkdkRUR09XMXFIdVBheUl0RGNDNVo0Q3BNSXdlSyt6NVp0aTJRZVowaEtTY0MxWXRwOHdVaEZyYWhyOXphaHd0Zmd6bVdvbzFOdWxFbXVjZWhWbTdaRzYwMlhnMDRKSkozbDh5TnFrelBaTXJxVGwxbUtMS285WHRpV2RmZDNUOFBLYlpOdTF2OUpKV0xvWVliY1hCQ0E9PTwvU2lnbmF0dXJlPgo8L0tleU9TQXV0aGVudGljYXRpb25YTUw+"
    }

    private val mPlayer by lazy { PlayerManager(this, PreferencesRepository(this)) }

    private val playlist = mapOf(
        KeyEvent.KEYCODE_1 to ("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd" to "https://proxy.uat.widevine.com/proxy?video_id=d286538032258a1c&provider=widevine_test"),
        KeyEvent.KEYCODE_2 to ("https://storage.googleapis.com/wvmedia/cbc1/h264/tears/tears_aes_cbc1_hd.mpd" to "https://proxy.uat.widevine.com/proxy?video_id=48fcc369939ac96c&provider=widevine_test")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launch {
            mPlayer.initExoPlayer(viewPlayer, licenseUrl, token)
            mPlayer.play(url)
        }
    }

    override fun onDestroy() {
        cancel()
        mPlayer.release()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val pair = playlist[keyCode]

        return if (pair == null) super.onKeyDown(keyCode, event) else {
            play(pair)
            return true
        }
    }

    private fun play(playlistPair: Pair<String, String>) {
        launch {
            mPlayer.release()
            mPlayer.initExoPlayer(viewPlayer, playlistPair.second, token)
            mPlayer.play(playlistPair.first)
        }
    }
}
