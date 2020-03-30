package com.hridin.exotesto.player

import android.content.Context
import android.media.MediaCodec
import android.os.Handler
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.hridin.exotesto.R
import com.hridin.exotesto.data.DrmInfo
import com.hridin.exotesto.data.DrmSystem
import com.hridin.exotesto.fromBase64
import com.hridin.exotesto.getMethodName
import com.hridin.exotesto.repository.PreferencesRepository
import com.hridin.exotesto.toBase64
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.*


class PlayerManager(private val context: Context, private val preferencesRepository: PreferencesRepository) : Player.EventListener {

    private var drmInfo: DrmInfo? = null
    private var exoPlayer: SimpleExoPlayer? = null
    private var drmSessionManager: DefaultDrmSessionManager<ExoMediaCrypto>? = null
    private var mediaDrmCallback: HttpMediaDrmCallback? = null
    private var loadControl = DefaultLoadControl.Builder()
        .createDefaultLoadControl()
    private val handler = Handler()
    private val dataSourceFactory = OkHttpDataSourceFactory(OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("http_interceptor").i(message)
            }

        }).setLevel(HttpLoggingInterceptor.Level.BODY))
        .build(), Util.getUserAgent(context, context.getString(R.string.app_name)))

    private var manifestUrl: String? = null
    private val timer = Timer()

    fun initExoPlayer(playerView: PlayerView) {
        this.drmInfo = drmInfo
        exoPlayer = SimpleExoPlayer.Builder(context)
                .setLoadControl(loadControl)
                .build()
        exoPlayer?.addListener(this)
        playerView.player = exoPlayer
    }

    fun release() {
        timer.cancel()
        exoPlayer?.removeListener(this)
        exoPlayer?.release()
    }

    fun play(url: String, drmInfo: DrmInfo?) {
        manifestUrl = url

        if (drmInfo != null) {
            val mapRequest = when (drmInfo.drmSystem) {
                DrmSystem.IRDETTO -> hashMapOf("Authorization" to "Bearer ${drmInfo.token}")
                DrmSystem.BUYDRM -> hashMapOf("customdata" to drmInfo.token)
            }

            drmSessionManager = buildDrmSessionManager(drmInfo.licenseUrl, mapRequest, drmInfo.persist)
            exoPlayer?.prepare(MediaSourceFactoryImpl.create(url, dataSourceFactory, drmSessionManager))
        } else {
            exoPlayer?.prepare(MediaSourceFactoryImpl.create(url, dataSourceFactory, DrmSessionManager.DUMMY))
        }

        exoPlayer?.playWhenReady = true
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)

        when (error.type) {
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

    private fun buildDrmSessionManager(
        licenseUrl: String,
        hashMap: HashMap<String, String>,
        persist: Boolean
    ): DefaultDrmSessionManager<ExoMediaCrypto> {
        mediaDrmCallback = HttpMediaDrmCallback(licenseUrl, dataSourceFactory)

        for (property in hashMap.entries) {
            mediaDrmCallback?.setKeyRequestProperty(property.key, property.value)
        }
        return DefaultDrmSessionManager.Builder()
            .setMultiSession(true)
            .setPersist(persist)
            .build(mediaDrmCallback!!)
            .also {
                it.addListener(handler, defaultDrmSessionEventListener)
                it.setOfflineLicenseRepository(offlineLicenseRepository)
            }
    }

    private val offlineLicenseRepository = object : DefaultDrmSession.OfflineLicenseRepository {
        override fun saveLicenseId(psshKey: ByteArray, licenseId: ByteArray, licenseDurationRemainingSec: Long) {
            preferencesRepository.setOfflineLicenseKeySetId(psshKey.toBase64(),
                licenseId.toBase64() + "::" + licenseDurationRemainingSec)
        }

        override fun getLicenseDurationRemainingSec(psshKey: ByteArray): Long {
            return preferencesRepository.getLicenseDurationRemainingSec(psshKey.toBase64()) ?: 0L
        }

        override fun getLicenseId(psshKey: ByteArray): ByteArray? {
            val licenseId = preferencesRepository.getOfflineLicenseKeySetId(psshKey.toBase64())
            return licenseId?.fromBase64()
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

        override fun onDrmSessionManagerError(error: Exception) {
            Timber.d(error, "=====> %s", getMethodName())
        }
    }
}