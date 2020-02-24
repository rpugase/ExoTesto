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
//            "https://streamer.vip.ministra.com/eurosport-buydrm/index.mpd"
            "https://test-irdeto.ministra.com/playlist.mpd" // irdeto
//            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"

        private const val licenseUrl =
//            "https://proxy.uat.widevine.com/proxy?video_id=48fcc369939ac96c&provider=widevine_test"
//            "https://proxy.uat.widevine.com/proxy?video_id=d286538032258a1c&provider=widevine_test"
//            "http://10.110.11.105:8090/"
//            "https://wv-keyos.licensekeyserver.com/"
            "https://dub-tctr.test.ott.irdeto.com/licenseServer/widevine/v1/informir/license?contentId=test-content"

        private const val token =
            "eyJraWQiOiIxZTk3NTcyNC01YmFkLTQ2ZGQtOGVlNi1kY2MyOWFkMzdiMjAiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI5MjY0YWNkYi1iYTY5LTRmNWYtYmVlZS03ODA4OTdhZmI3MmQiLCJzdWIiOiJiYWNrZW5kX3Rlc3RfMiIsImVudCI6W3siZXBpZCI6ImRlZmF1bHQiLCJiaWQiOiJkZWZhdWx0In1dLCJpc2UiOnRydWUsImFpZCI6ImluZm9ybWlyIiwiaWF0IjoxNTc0MjYwODUwLCJleHAiOjE2NzQyNjI2NTAsImlzcyI6IlRlc3QgTWluaXN0cmEgUG9ydGFsIn0.7GwV0Z5jGBja5Ds5mP18fs8NaNkc0LHxpkInqO8lMtg"
//            "PEtleU9TQXV0aGVudGljYXRpb25YTUw+CjxEYXRhPgogIDxHZW5lcmF0aW9uVGltZT4yMDIwLTAxLTIzIDEwOjUwOjIzLjAxMTwvR2VuZXJhdGlvblRpbWU+CiAgPEV4cGlyYXRpb25UaW1lPjIwMzAtMDEtMjAgMTA6NTA6MjMuMDExPC9FeHBpcmF0aW9uVGltZT4KICA8VW5pcXVlSWQ+NDg3ZjdiMjJmNjgzMTJkMmMxYmJjOTNiMWFlYTQ0NWI8L1VuaXF1ZUlkPgogIDxSU0FQdWJLZXlJZD5jYTMzZWU5YmQwOGI4YTZiMjIxYjcwMzA3NDA4OTZlMDwvUlNBUHViS2V5SWQ+CiAgPFdpZGV2aW5lUG9saWN5IGZsX0NhblBsYXk9InRydWUiIGZsX0NhblBlcnNpc3Q9ImZhbHNlIiAvPgogIDxXaWRldmluZUNvbnRlbnRLZXlTcGVjIFRyYWNrVHlwZT0iSEQiPgogICAgPFNlY3VyaXR5TGV2ZWw+MTwvU2VjdXJpdHlMZXZlbD4KICA8L1dpZGV2aW5lQ29udGVudEtleVNwZWM+CjwvRGF0YT4KPFNpZ25hdHVyZT5LT29RQXJ4ZnQ1WEpKY3lBVWI1SjZvUyt5dnNoSi9qV04wa3ZyVXRJZmdhbDU2NXNnRi8zdVMweVVVZnUwMkFZNVRCaytMYXZlQkZDTWxENFZPZmU2VTBIYTdXV3UzeWFKMlFKeW9nMlI4TFN4N2JCemNuMXF5Y0NmRGpERGpram5rR29CbVNPTm1PQ2VCcVJFamxRUUFxa0UrQVBYZzAvS3oxL3k3MVVwcGVvRDZVUS9hWXptenhiQmQyVzVma1FJRWVYTngxWWFSc3I2R2Z0NGI2Q2pNNVNRRVN6SlhCajZpRzV4aFlER2hYVzlCaUZMcFh0cnUrZVVaUlowM0ZOY2tmV2dhTlpnNWVjQkVOYlJyd0llc2psRnQ4MjRIUnB3aXpPN1ZYVVFMN0VESWx5czV2S2IrMHhROUVNZVhORkVYcVR0NHdTVFA2bXJhMGZYdjJ0OEE9PTwvU2lnbmF0dXJlPgo8L0tleU9TQXV0aGVudGljYXRpb25YTUw+"

        private val drmSystem = DrmSystem.IRDETTO
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
            mPlayer.initExoPlayer(viewPlayer, licenseUrl, token, drmSystem)
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
            mPlayer.initExoPlayer(viewPlayer, playlistPair.second, token, drmSystem)
            mPlayer.play(playlistPair.first)
        }
    }
}
