package ru.serzh272.farmer

import android.app.Application
import android.content.Context
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

private const val KEY = "281b05b1-7d4c-4c28-93fd-895d11975a04"

@HiltAndroidApp
class App: Application() {
    companion object{
        private var instance:App? = null
        fun applicationContext():Context{
            return instance!!.applicationContext
        }
    }
    init {
        MapKitFactory.setApiKey(KEY)
        instance = this
    }

}

data class AppSettings(
    val initLatitude: Double,
    val initLongitude: Double
)