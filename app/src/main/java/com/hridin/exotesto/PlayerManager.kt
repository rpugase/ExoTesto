package com.hridin.exotesto

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.source.dash.DashUtil
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.*

class PlayerManager(private val context: Context, private val preferensesRepository: PreferensesRepository) : Player.EventListener {

    private var exoPlayer: SimpleExoPlayer? = null
    private var drmSessionManager: DefaultDrmSessionManager<FrameworkMediaCrypto>? = null
    private var mediaDrmCallback: HttpMediaDrmCallback? = null
    private val handler = Handler()
    private val dataSourceFactory: DefaultHttpDataSourceFactory by lazy { DefaultHttpDataSourceFactory(Util.getUserAgent(context, context.getString(R.string.app_name))) }

    fun initExoPlayer(playerView: PlayerView, licenseUrl: String, token: String) {
        val rendersFactory = DefaultRenderersFactory(context)
        val trackSelector = DefaultTrackSelector()
        drmSessionManager = buildDrmSessionManager(licenseUrl, hashMapOf("customdata" to token))

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, rendersFactory, trackSelector, drmSessionManager)
        playerView.player = exoPlayer
    }

    fun release() {
        exoPlayer?.release()
    }

    suspend fun play(url: String) {
        val offlineLicenseKeySetId = preferensesRepository.offlineLicenseKeySetId ?: downloadLicense(url)

        Timber.d(Arrays.toString(offlineLicenseKeySetId))

        drmSessionManager?.setMode(DefaultDrmSessionManager.MODE_PLAYBACK, offlineLicenseKeySetId)
        exoPlayer?.prepare(MediaSourceFactoryImpl.create(url, dataSourceFactory))
        exoPlayer?.playWhenReady = true
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        super.onPlayerError(error)
        Timber.d(error)
    }

    private fun buildDrmSessionManager(licenseUrl: String, hashMap: HashMap<String, String>): DefaultDrmSessionManager<FrameworkMediaCrypto> {
        mediaDrmCallback = HttpMediaDrmCallback(licenseUrl, dataSourceFactory)

        for (property in hashMap.entries) {
            mediaDrmCallback?.setKeyRequestProperty(property.key, property.value)
        }

        return DefaultDrmSessionManager.newWidevineInstance(mediaDrmCallback, null)
            .also {
                it.addListener(handler, defaultDrmSessionEventListener)
            }
    }

    @Throws(IOException::class, InterruptedException::class)
    private suspend fun getDrmInitData(contentUrl: String): DrmInitData? = withContext(Dispatchers.IO) {
        val dataSource = dataSourceFactory.createDataSource()
        val manifest = DashUtil.loadManifest(dataSource, Uri.parse(contentUrl))
        val period = manifest.getPeriod(0)
        Timber.d("period.id = %s", period.id)
        DashUtil.loadDrmInitData(dataSource, manifest.getPeriod(0))
    }

    private suspend fun downloadLicense(urlManifest: String): ByteArray? {
        val offlineLicenseHelper = OfflineLicenseHelper(
            C.WIDEVINE_UUID,
            FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), mediaDrmCallback, null)

        var drmInitData: DrmInitData? = null
        try {
            drmInitData = getDrmInitData(urlManifest)
        } catch (e: IOException) {
            Timber.d(e)
        } catch (e: InterruptedException) {
            Timber.d(e)
        }
        Timber.d("drmInitData=%s", drmInitData.toString())
        return if (drmInitData != null) offlineLicenseHelper.downloadLicense(drmInitData).apply { preferensesRepository.offlineLicenseKeySetId = this }
        else null
    }

    private val defaultDrmSessionEventListener = object : DefaultDrmSessionEventListener {
        override fun onDrmKeysRestored() {
            Timber.d("=====> %s", getMethodName())
        }

        override fun onDrmKeysLoaded() {
            Timber.d("=====> %s", getMethodName())
        }

        override fun onDrmKeysRemoved() {
            Timber.d("=====> %s", getMethodName())
        }

        override fun onDrmSessionManagerError(error: Exception?) {
            Timber.d(error, "=====> %s", getMethodName())
        }
    }
}