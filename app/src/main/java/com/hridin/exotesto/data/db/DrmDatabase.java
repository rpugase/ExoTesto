package com.hridin.exotesto.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DrmStreamEntity.class}, version = 1)
public abstract class DrmDatabase extends RoomDatabase {
    public abstract DrmRepository drmRepository();
}
