package com.spatialapps.spatialmemoryanchors.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.spatialapps.spatialmemoryanchors.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
