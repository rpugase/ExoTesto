package com.hridin.exotesto;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

class MediaSourceFactoryImpl {

    static MediaSource create(String url, DefaultHttpDataSourceFactory dataSourceFactory) {
        if (TextUtils.isEmpty(url)) return null;
        if (url.startsWith("/")) {
            url = "file://" + url;
        }

        final MediaSource mediaSource;
        final int sourceType = Util.inferContentType(url);
        final Uri uri = Uri.parse(url);

        switch (sourceType) {
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_OTHER:
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                break;
            default:
                return null;
        }

        return mediaSource;
    }
}
