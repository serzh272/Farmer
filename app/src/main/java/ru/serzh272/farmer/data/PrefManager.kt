package ru.serzh272.farmer.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import ru.serzh272.farmer.App
import ru.serzh272.farmer.AppSettings
import ru.serzh272.farmer.data.delegates.PrefDelegate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class PrefManager(context: Context = App.applicationContext()) {

    val settings: Flow<AppSettings>
        get() {
            val initLatitude = dataStore.data.map { it[doublePreferencesKey(this::latitude.name)] ?: 52.058035 }
            val initLongitude = dataStore.data.map { it[doublePreferencesKey(this::longitude.name)] ?: 113.485333 }
            return initLatitude.zip(initLongitude){lat, lon -> AppSettings(lat, lon)}
                .distinctUntilChanged()
        }
    val dataStore = context.dataStore
    private val errHandler = CoroutineExceptionHandler { _, th ->
        Log.d("M_PrefManager", "err ${th.message}")
    }
    internal val scope = CoroutineScope(SupervisorJob() + errHandler)
    var latitude by PrefDelegate(52.058035)
    var longitude by PrefDelegate(113.485333)
}