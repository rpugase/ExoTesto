package com.hridin.exotesto.player;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.Nullable;

public class MediaSourceFactoryImpl {

    static MediaSource create(String url, DataSource.Factory dataSourceFactory, @Nullable DefaultDrmSessionManager<ExoMediaCrypto> drmSessionManager) {
        if (TextUtils.isEmpty(url)) return null;
        if (url.startsWith("/")) {
            url = "file://" + url;
        }

        final MediaSource mediaSource;
        final int sourceType = Util.inferContentType(url);
        final Uri uri = Uri.parse(url);

        switch (sourceType) {
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
                break;
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
                break;
            case C.TYPE_OTHER:
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
                break;
            default:
                return null;
        }

        return mediaSource;
    }
}
