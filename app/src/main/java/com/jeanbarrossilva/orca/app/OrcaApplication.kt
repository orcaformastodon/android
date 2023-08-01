package com.jeanbarrossilva.orca.app

import android.app.Application
import android.content.Context
import androidx.annotation.Discouraged
import androidx.core.content.edit
import com.jeanbarrossilva.orca.platform.launchable.Launchable
import net.time4j.android.ApplicationStarter
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

internal open class OrcaApplication : Application(), Launchable {
    private val preferences
        get() = getSharedPreferences("orca", Context.MODE_PRIVATE)

    override fun onCreate() {
        super.onCreate()

        @Suppress("DiscouragedApi")
        markAsLaunched()

        setUpInjection()
        ApplicationStarter.initialize(this, true)
    }

    override fun count(): Int {
        return preferences.getInt(LAUNCH_COUNT_PREFERENCE_KEY, 0)
    }

    @Discouraged("Should only be called by OrcaApplication internally.")
    override fun markAsLaunched() {
        preferences.edit {
            putInt(LAUNCH_COUNT_PREFERENCE_KEY, count() + 1)
        }
    }

    private fun setUpInjection() {
        startKoin {
            androidContext(this@OrcaApplication)
        }
    }

    companion object {
        private const val LAUNCH_COUNT_PREFERENCE_KEY = "launch_count"
    }
}