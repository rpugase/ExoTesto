package com.hridin.exotesto

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.dash.DashUtil
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import timber.log.Timber
import java.io.IOException
import java.util.*

class PlayerManager(private val context: Context, private val preferencesRepository: PreferencesRepository) : Player.EventListener {

    private var exoPlayer: SimpleExoPlayer? = null
    private var drmSessionManager: DefaultDrmSessionManager<FrameworkMediaCrypto>? = null
    private var mediaDrmCallback: HttpMediaDrmCallback? = null
    private val handler = Handler()
    private val dataSourceFactory = OkHttpDataSourceFactory(OkHttpClient.Builder()
        .addInterceptor {
            val request = it.request()

            if (request.url.toString() == "https://wv-keyos.licensekeyserver.com/") {
                val buffer = Buffer()
                request.newBuilder().build().body?.writeTo(buffer)
//                Timber.d(buffer.size.toString())
            }

            it.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("http_interceptor").i(message)
            }

        }).setLevel(HttpLoggingInterceptor.Level.BODY))
        .build(), Util.getUserAgent(context, context.getString(R.string.app_name)))

    private var manifestUrl: String? = null

    fun initExoPlayer(playerView: PlayerView, licenseUrl: String, token: String, drmSystem: DrmSystem) {
        val mapRequest = when (drmSystem) {
            DrmSystem.IRDETTO -> hashMapOf("Authorization" to "Bearer $token")
            DrmSystem.BUYDRM -> hashMapOf("customdata" to token)
        }

        val rendersFactory = DefaultRenderersFactory(context)
        val trackSelector = DefaultTrackSelector()
        drmSessionManager = buildDrmSessionManager(licenseUrl, mapRequest)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, rendersFactory, trackSelector, drmSessionManager)
        exoPlayer?.addListener(this)
        playerView.player = exoPlayer
    }

    fun release() {
        exoPlayer?.removeListener(this)
        exoPlayer?.release()
    }

    suspend fun play(url: String) {
        manifestUrl = url
        val offlineLicenseKeySetId = preferencesRepository.getOfflineLicenseKeySetId(url) ?: downloadLicense(url)

        Timber.d("OFFLINE:licenseId=%s", offlineLicenseKeySetId?.contentToString())

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

    private suspend fun downloadLicense(urlManifest: String): ByteArray? {
        val offlineLicenseHelper = OfflineLicenseHelper(C.WIDEVINE_UUID, FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), mediaDrmCallback, null)

        var drmInitData: DrmInitData? = null
        try {
            drmInitData = getDrmInitData(urlManifest)
        } catch (e: IOException) {
            Timber.d(e)
        } catch (e: InterruptedException) {
            Timber.d(e)
        }
        Timber.d("Online:drmInitData=%s", drmInitData)
        return if (drmInitData != null) offlineLicenseHelper.downloadLicense(drmInitData).apply { preferencesRepository.setOfflineLicenseKeySetId(urlManifest, this) }
        else null
    }

    @Throws(IOException::class, InterruptedException::class)
    private suspend fun getDrmInitData(manifestUrl: String): DrmInitData? = withContext(Dispatchers.IO) {
        val dataSource = dataSourceFactory.createDataSource()
        val manifest = DashUtil.loadManifest(dataSource, Uri.parse(manifestUrl))
        val period = manifest.getPeriod(0)
        Timber.d("period.id = %s", period.id)

//        val representation = manifest.getPeriod(0).adaptationSets.first().representations.first()

//        val dashSegmentIndex = (representation.index as Representation.MultiSegmentRepresentation)

        DashUtil.loadDrmInitData(dataSource, manifest.getPeriod(0))// ?: dashSegmentIndex.format.drmInitData
    }
}