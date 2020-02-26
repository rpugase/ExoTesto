package com.hridin.exotesto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        private const val url =
            "https://streamer.vip.ministra.com/buydrm_sameid_ictv/index.mpd" // buydrm
//            "https://streamer.vip.ministra.com/eurosport-buydrm/index.mpd" // buydrm
//            "https://streamer.vip.ministra.com/buydrm/stars_h264_1280p.mp4/index.mpd" // buydrm VOD

        private const val licenseUrl =
//            "http://10.110.11.105:8090/"
            "https://wv-keyos.licensekeyserver.com/" // buydrm
//            "https://dub-tctr.test.ott.irdeto.com/licenseServer/widevine/v1/informir/license?contentId=test-content" // irdetto

        private const val token =
//            "PEtleU9TQXV0aGVudGljYXRpb25YTUw+CjxEYXRhPgogIDxHZW5lcmF0aW9uVGltZT4yMDIwLTAxLTIzIDEwOjUwOjIzLjAxMTwvR2VuZXJhdGlvblRpbWU+CiAgPEV4cGlyYXRpb25UaW1lPjIwMzAtMDEtMjAgMTA6NTA6MjMuMDExPC9FeHBpcmF0aW9uVGltZT4KICA8VW5pcXVlSWQ+NDg3ZjdiMjJmNjgzMTJkMmMxYmJjOTNiMWFlYTQ0NWI8L1VuaXF1ZUlkPgogIDxSU0FQdWJLZXlJZD5jYTMzZWU5YmQwOGI4YTZiMjIxYjcwMzA3NDA4OTZlMDwvUlNBUHViS2V5SWQ+CiAgPFdpZGV2aW5lUG9saWN5IGZsX0NhblBsYXk9InRydWUiIGZsX0NhblBlcnNpc3Q9ImZhbHNlIiAvPgogIDxXaWRldmluZUNvbnRlbnRLZXlTcGVjIFRyYWNrVHlwZT0iSEQiPgogICAgPFNlY3VyaXR5TGV2ZWw+MTwvU2VjdXJpdHlMZXZlbD4KICA8L1dpZGV2aW5lQ29udGVudEtleVNwZWM+CjwvRGF0YT4KPFNpZ25hdHVyZT5LT29RQXJ4ZnQ1WEpKY3lBVWI1SjZvUyt5dnNoSi9qV04wa3ZyVXRJZmdhbDU2NXNnRi8zdVMweVVVZnUwMkFZNVRCaytMYXZlQkZDTWxENFZPZmU2VTBIYTdXV3UzeWFKMlFKeW9nMlI4TFN4N2JCemNuMXF5Y0NmRGpERGpram5rR29CbVNPTm1PQ2VCcVJFamxRUUFxa0UrQVBYZzAvS3oxL3k3MVVwcGVvRDZVUS9hWXptenhiQmQyVzVma1FJRWVYTngxWWFSc3I2R2Z0NGI2Q2pNNVNRRVN6SlhCajZpRzV4aFlER2hYVzlCaUZMcFh0cnUrZVVaUlowM0ZOY2tmV2dhTlpnNWVjQkVOYlJyd0llc2psRnQ4MjRIUnB3aXpPN1ZYVVFMN0VESWx5czV2S2IrMHhROUVNZVhORkVYcVR0NHdTVFA2bXJhMGZYdjJ0OEE9PTwvU2lnbmF0dXJlPgo8L0tleU9TQXV0aGVudGljYXRpb25YTUw+"
            "PD94bWwgdmVyc2lvbj0iMS4wIj8+CjxLZXlPU0F1dGhlbnRpY2F0aW9uWE1MPjxEYXRhPjxXaWRldmluZVBvbGljeSBmbF9DYW5QZXJzaXN0PSJ0cnVlIiBmbF9DYW5QbGF5PSJ0cnVlIi8+PFdpZGV2aW5lQ29udGVudEtleVNwZWMgVHJhY2tUeXBlPSJIRCI+PFNlY3VyaXR5TGV2ZWw+MTwvU2VjdXJpdHlMZXZlbD48L1dpZGV2aW5lQ29udGVudEtleVNwZWM+PFVzZXJuYW1lPjAwMDAwMTwvVXNlcm5hbWU+PEdlbmVyYXRpb25UaW1lPjIwMjAtMDItMjQgMTY6MDA6MzMuMDAwPC9HZW5lcmF0aW9uVGltZT48RXhwaXJhdGlvblRpbWU+MjAyMC0wMy0yNSAxNjowNTozMy4wMDA8L0V4cGlyYXRpb25UaW1lPjxVbmlxdWVJZD5lZjlkMGFmZjJjODU2MzhkMzBjMTc5MzhjYjc3ZDBhNTwvVW5pcXVlSWQ+PFJTQVB1YktleUlkPmNhMzNlZTliZDA4YjhhNmIyMjFiNzAzMDc0MDg5NmUwPC9SU0FQdWJLZXlJZD48L0RhdGE+PFNpZ25hdHVyZT5oc3JjVy9yV1U1SHlTdEU4eGZPZmZnRFpXaXIrN3RwcWlzNHgxWCtEZDRsNVIvakpwWkswRW9zNS9hcHFVZXh3blRORDRzQnVhWkFrbzA3WXBOajJ6SEd5OUZPcnRGM0U2TUNqVFEva0N3ekxJaWNIajgvTURhSmFkS1ZqNHZRcWV4Nlh5dmVLdE5KOWc3dVBpQXZBV3pMTk5lRlVoVWlvcFBPZXIvOW1nOVZMekFYNldsa1p6QjFWeGJOVUJMaXBTU3FaNFMxWUZpTkdJQkJhdUNpamllZW9FMTVxaG40S2hEWjNDVWY1NHN1SXNYMUwvOE9vQ3hnTk1GZTdKZEJPbFdPVlRlclk3b2NoRDFqOUx2OWIreWNDVFVFb3JNMm9wTEIxZ0l2OERJRC9rVHpyVnQxZ0UwY0xiYXZYZVJuMFpMNVI0T0pVMWlxMDFuRktmYlBvWVE9PTwvU2lnbmF0dXJlPjwvS2V5T1NBdXRoZW50aWNhdGlvblhNTD4K"
        private val drmSystem = DrmSystem.BUYDRM
    }

    private val mPlayer by lazy { PlayerManager(this, PreferencesRepository(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPlayer.initExoPlayer(viewPlayer, licenseUrl, token, drmSystem)
        mPlayer.play(url)
    }

    override fun onDestroy() {
        cancel()
        mPlayer.release()
        super.onDestroy()
    }
}
