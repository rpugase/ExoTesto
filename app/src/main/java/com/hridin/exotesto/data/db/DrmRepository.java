package com.hridin.exotesto.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DrmRepository {

    @Query("SELECT * FROM drm_stream")
    List<DrmStreamEntity> getAll();

    @Query("SELECT * FROM drm_stream WHERE pssh = :pssh")
    DrmStreamEntity getByPssh(String pssh);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DrmStreamEntity employee);

    @Update
    void update(DrmStreamEntity employee);

    @Delete
    void delete(DrmStreamEntity employee);

    @Query("DELETE FROM drm_stream")
    void deleteAll();
}
