package com.hridin.exotesto.player

import android.content.Context
import android.media.MediaCodec
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.hridin.exotesto.R
import com.hridin.exotesto.data.DrmSystem
import com.hridin.exotesto.getMethodName
import com.hridin.exotesto.player.drm.CustomDrmSessionManager
import com.hridin.exotesto.repository.PreferencesRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.*


class PlayerManager(private val context: Context, private val preferencesRepository: PreferencesRepository) : Player.EventListener {

    private var exoPlayer: SimpleExoPlayer? = null
    private var drmSessionManager: CustomDrmSessionManager<FrameworkMediaCrypto>? = null
    private var mediaDrmCallback: HttpMediaDrmCallback? = null
    private val handler = Handler()
    private val dataSourceFactory = OkHttpDataSourceFactory(OkHttpClient.Builder()
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

    fun play(url: String) {
        manifestUrl = url

        exoPlayer?.prepare(MediaSourceFactoryImpl.create(url, dataSourceFactory))
        exoPlayer?.playWhenReady = true
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        super.onPlayerError(error)

        when (error?.type) {
            ExoPlaybackException.TYPE_OUT_OF_MEMORY -> {
                Timber.d(error, "=====> %s TYPE_OUT_OF_MEMORY", getMethodName())
            }
            ExoPlaybackException.TYPE_REMOTE -> {
                Timber.d(error, "=====> %s TYPE_REMOTE", getMethodName())

            }
            ExoPlaybackException.TYPE_RENDERER -> {
                Timber.d(error, "=====> %s TYPE_RENDERER", getMethodName())

                if (error.rendererException is MediaCodec.CryptoException) {
//                    drmSessionManager?.forceOnlineOneTime()
//                    TODO("Reinitialize player")
                }
            }
            ExoPlaybackException.TYPE_SOURCE -> {
                Timber.d(error, "=====> %s TYPE_SOURCE", getMethodName())

            }
            ExoPlaybackException.TYPE_UNEXPECTED -> {
                Timber.d(error, "=====> %s TYPE_UNEXPECTED", getMethodName())
            }
            else -> return
        }
    }

    private fun buildDrmSessionManager(licenseUrl: String, hashMap: HashMap<String, String>): CustomDrmSessionManager<FrameworkMediaCrypto> {
        mediaDrmCallback = HttpMediaDrmCallback(licenseUrl, dataSourceFactory)

        for (property in hashMap.entries) {
            mediaDrmCallback?.setKeyRequestProperty(property.key, property.value)
        }

        return CustomDrmSessionManager.newWidevineInstance(mediaDrmCallback, null)
            .also {
                it.addListener(handler, defaultDrmSessionEventListener)
                it.setOfflineLicenseRepository(offlineLicenseRepository)
            }
    }

    private val offlineLicenseRepository = object : CustomDrmSessionManager.OfflineLicenseRepository {
        override fun saveLicenseId(psshKey: String, licenseId: ByteArray) {
            preferencesRepository.setOfflineLicenseKeySetId(psshKey, licenseId)
        }

        override fun getLicenseId(psshKey: String): ByteArray? {
            return preferencesRepository.getOfflineLicenseKeySetId(psshKey)
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
}