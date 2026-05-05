package com.mathbank

import android.app.Application
import androidx.work.Configuration
import com.mathbank.data.db.AppDatabase

class MathBankApp : Application(), Configuration.Provider {

    companion object {
        lateinit var instance: MathBankApp
            private set
    }

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
