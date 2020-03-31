package com.hridin.exotesto

import android.app.Application
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.hridin.exotesto.data.db.DrmDatabase
import com.hridin.exotesto.data.db.DrmRepository
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    private lateinit var database: DrmDatabase

    val drmRepository: DrmRepository
        get() = database.drmRepository()

    override fun onCreate() {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this)
        database = Room.databaseBuilder(this, DrmDatabase::class.java, "drm_database")
            .allowMainThreadQueries()
            .build()
    }
}