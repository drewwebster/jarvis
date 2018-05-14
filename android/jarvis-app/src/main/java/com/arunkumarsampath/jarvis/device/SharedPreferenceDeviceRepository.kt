package com.arunkumarsampath.jarvis.device

import android.content.SharedPreferences
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceDeviceRepository
@Inject
constructor(private val sharedPreferences: SharedPreferences) : DeviceRepository {

    override var deviceDocked: Boolean
        get() = sharedPreferences.getBoolean(DEVICE_DOCKED_KEY, false)
        set(value) {
            Timber.d("Docked mode : $value")
            sharedPreferences.edit().putBoolean(DEVICE_DOCKED_KEY, value).apply()
        }

    companion object {
        const val DEVICE_DOCKED_KEY = "device_docked"
    }
}