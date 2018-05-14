package com.arunkumarsampath.jarvis.device

import dagger.Module
import dagger.Provides

@Module
class DeviceModule {

    @Provides
    fun deviceRepo(sharedPreferenceDeviceRepository: SharedPreferenceDeviceRepository): DeviceRepository = sharedPreferenceDeviceRepository
}