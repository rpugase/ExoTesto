package com.hridin.exotesto.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drm_stream")
public class DrmStreamEntity {

    public DrmStreamEntity(@NonNull String pssh, String licenseId, String contentType, String licenseDurationDate) {
        this.pssh = pssh;
        this.licenseId = licenseId;
        this.contentType = contentType;
        this.licenseDurationDate = licenseDurationDate;
    }

    @NonNull
    @PrimaryKey
    public String pssh;
    public String licenseId;
    public String contentType; // to enum stream or vod
    public String licenseDurationDate; // UTC
}
